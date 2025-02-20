package com.x.general.assemble.control.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

public class PDFKeyWordPosition extends PDFTextStripper {

    private List<String> keywordList;
    private Map<String, List<Position>> positionListMap;

    public PDFKeyWordPosition() {
        super();
    }

    // 获取坐标信息
    public Map<String, List<Position>> getCoordinate(List<String> keywordList, PDDocument document) throws IOException {
        super.setSortByPosition(true);
        this.keywordList = keywordList;
        this.positionListMap = new HashMap<>();
        super.setStartPage(1);
        super.setEndPage(1);
        Writer dummy = new OutputStreamWriter(new ByteArrayOutputStream());
        super.writeText(document, dummy);
        return positionListMap;
    }

    @Override
    protected void writeString(String string, List<TextPosition> textPositions) {
        for (String keyword : keywordList) {
            int foundIndex = 0;
            List<Position> positionList = positionListMap.computeIfAbsent(keyword, k -> new ArrayList<>());
            for (int i = 0; i < textPositions.size(); i++) {
                TextPosition textPosition = textPositions.get(i);
                String str = textPosition.getUnicode();
                if (StringUtils.isNotEmpty(str) && str.charAt(0) == keyword.charAt(foundIndex)) {
                    foundIndex++;
                    int count = foundIndex;
                    for (int j = foundIndex; j < keyword.length(); j++) {
                        if (i + j >= textPositions.size()) {
                            break;
                        } else {
                            String s = textPositions.get(i + j).getUnicode();
                            if (StringUtils.isNotEmpty(s) && s.charAt(0) == keyword.charAt(j)) {
                                count++;
                            }
                        }
                    }
                    if (count == keyword.length()) {
                        foundIndex = 0;
                        Position position = new Position();
                        position.setX(textPosition.getX());
                        position.setY(textPosition.getY());
                        positionList.add(position);
                        positionListMap.put(keyword, positionList);
                    }
                }
            }
        }
    }

}

class Position {
    public Position() {
    }

    public Position(float x, float y) {
        super();
        this.x = x;
        this.y = y;
    }

    float x;
    float y;

    /**
     * @return the x
     */
    public float getX() {
        return x;
    }

    /**
     * @param x
     *            the x to set
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public float getY() {
        return y;
    }

    /**
     * @param y
     *            the y to set
     */
    public void setY(float y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "Position [x=" + x + ", y=" + y + "]";
    }
}
