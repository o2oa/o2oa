package com.x.query.assemble.surface.jaxrs.index;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopFieldCollector;
import org.apache.lucene.store.Directory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.common.collect.Streams;
import com.google.gson.JsonElement;
import com.hankcs.lucene.HanLPAnalyzer;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.tuple.Pair;
import com.x.base.core.project.bean.tuple.Quadruple;
import com.x.base.core.project.bean.tuple.Quintuple;
import com.x.base.core.project.bean.tuple.Triple;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.general.core.entity.GeneralFile;
import com.x.query.assemble.surface.Business;
import com.x.query.assemble.surface.ThisApplication;
import com.x.query.core.express.index.Indexs;
import com.x.query.core.express.jaxrs.index.ActionExportWi;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionExport extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionExport.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

        LOGGER.info("execute:{}.", effectivePerson::getDistinguishedName);

        ActionResult<Wo> result = new ActionResult<>();
        Wo wo = new Wo();
        result.setData(wo);

        Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

        String category = wi.getCategory();
        String type = wi.getType();
        String key = wi.getKey();

        List<String> readers = new ArrayList<>();
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            String person = business.index().who(effectivePerson, wi.getPerson());
            readers = business.index().determineReaders(person, category, type, key);
        }

        Optional<Query> searchQuery = searchQuery(wi.getQuery(), new HanLPAnalyzer());
        Optional<Query> readersQuery = Indexs.readersQuery(readers);
        List<Query> filterQueries = Indexs.filterQueries(wi.getFilterList());
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        Stream.of(searchQuery, readersQuery).filter(Optional::isPresent)
                .forEach(o -> builder.add(o.get(), BooleanClause.Occur.MUST));
        filterQueries.stream().forEach(o -> builder.add(o, BooleanClause.Occur.MUST));
        Query query = builder.build();
        LOGGER.debug("index export lucene query:{}.", query::toString);
        Optional<Directory> optional = Indexs.directory(category, type, key, true);
        if (optional.isEmpty()) {
            throw new ExceptionDirectoryNotExist();
        }
        try (DirectoryReader reader = DirectoryReader.open(optional.get()); Workbook workbook = new XSSFWorkbook()) {
            List<Triple<String, String, String>> outFields = outFields(type, wi.getFixedFieldList(),
                    wi.getDynamicFieldList());
            IndexSearcher searcher = new IndexSearcher(reader);
            TopFieldCollector topFieldCollector = TopFieldCollector.create(sort(wi.getSort()),
                    Config.query().index().getSearchMaxHits(), Config.query().index().getSearchMaxHits());
            searcher.search(query, topFieldCollector);
            Sheet sheet = createSheet(workbook, outFields);
            ScoreDoc[] scoreDocs = topFieldCollector.topDocs().scoreDocs;
            if (null != scoreDocs) {
                CellStyle cellStyle = valueCellStyle(workbook);
                Arrays.stream(
                        scoreDocs).<Quadruple<List<Triple<String, String, String>>, org.apache.lucene.document.Document, Sheet, CellStyle>>map(
                                o -> {
                                    try {
                                        org.apache.lucene.document.Document document = searcher.doc(o.doc);
                                        return Quadruple.of(outFields, document, sheet, cellStyle);
                                    } catch (Exception e) {
                                        LOGGER.error(e);
                                    }
                                    return Quadruple.of(outFields, null, sheet, cellStyle);
                                })
                        .filter(o -> !Objects.isNull(o.second())).forEach(documentExportConsumer);
            }
            sheet.setAutobreaks(true);
            for (int i = 0; i < outFields.size(); i++) {
                sheet.autoSizeColumn(i);
            }
            try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                workbook.write(baos);
                String name = DateTools.format(new Date(), DateTools.formatCompact_yyyyMMddHHmmss) + ".xlsx";
                StorageMapping mapping = ThisApplication.context().storageMappings().random(GeneralFile.class);
                GeneralFile generalFile = new GeneralFile(mapping.getName(), name,
                        effectivePerson.getDistinguishedName());
                generalFile.saveContent(mapping, baos.toByteArray(), name);
                emc.beginTransaction(GeneralFile.class);
                emc.persist(generalFile, CheckPersistType.all);
                emc.commit();
                wo.setId(generalFile.getId());
            }
        }
        return result;
    }

    private List<Triple<String, String, String>> outFields(String type, List<String> fixedFieldList,
            List<String> dynamicFieldList) {
        List<Triple<String, String, String>> list = new ArrayList<>();
        if (ListTools.isEmpty(fixedFieldList) && ListTools.isEmpty(dynamicFieldList)) {
            list.addAll(getFixedFieldList(Indexs.CATEGORY_PROCESSPLATFORM, type).stream()
                    .map(o -> Triple.of(o.getField(), o.getName(), o.getFieldType())).collect(Collectors.toList()));
        } else {
            if (!ListTools.isEmpty(fixedFieldList)) {
                fixedFieldList.stream().forEach(o -> list.add(Indexs.judgeField(o)));
            }
            if (!ListTools.isEmpty(dynamicFieldList)) {
                dynamicFieldList.stream().forEach(o -> list.add(Indexs.judgeField(o)));
            }
        }
        return list;
    }

    private static CellStyle valueCellStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(false);
        cellStyle.setFont(font);
        cellStyle.setWrapText(true);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        return cellStyle;
    }

    private static Sheet createSheet(Workbook workbook, List<Triple<String, String, String>> list) {
        Sheet sheet = workbook.createSheet();
        Row row = sheet.createRow(0);
        CellStyle cellStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        cellStyle.setFont(font);
        cellStyle.setWrapText(true);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        Streams.mapWithIndex(list.stream(), Pair::of).forEach(p -> {
            Cell cell = row.createCell(p.second().intValue());
            cell.setCellValue(p.first().second());
            cell.setCellStyle(cellStyle);
        });
        // auto row size
        // row.setHeight((short) 0);
        return sheet;
    }

    private static Consumer<Quadruple<List<Triple<String, String, String>>, org.apache.lucene.document.Document, Sheet, CellStyle>> documentExportConsumer = param -> {
        final Row row = param.third().createRow(param.third().getLastRowNum() + 1);
        Streams.mapWithIndex(param.first().stream(), Pair::of)
                .map(o -> Quintuple.of(o.first().first(), o.first().second(), o.first().third(), o.second(),
                        param.second().getField(o.first().first())))
                .filter(o -> !Objects.isNull(o.fifth())).forEach(o -> {
                    Object value = indexableFieldValue(o.fifth(), o.third());
                    Cell cell = row.createCell(o.fourth().intValue());
                    cell.setCellValue(Objects.toString(value, ""));
                    cell.setCellStyle(param.fourth());
                });
        // auto row size
        // row.setHeight((short) 0);
    };

    @Schema(name = "com.x.custom.index.assemble.control.jaxrs.search.ActionExport$Wo")
    public static class Wo extends WoId {

        private static final long serialVersionUID = 902681475422445346L;

    }

    @Schema(name = "com.x.custom.index.assemble.control.jaxrs.search.ActionExport$Wi")
    public class Wi extends ActionExportWi {

        private static final long serialVersionUID = -4646809016933808952L;

    }

}