package com.x.query.core.entity;

import com.x.base.core.entity.AbstractPersistenceProperties;

public final class PersistenceProperties extends AbstractPersistenceProperties {

    public static class Query {

        private Query() {
            // nothing
        }

        public static final String TABLE = "QRY_QUERY";
    }

    public static class View {

        private View() {
            // nothing
        }

        public static final String table = "QRY_VIEW";
    }

    public static class Import {

        private Import() {
            // nothing
        }

        public static class ImportModel {

            private ImportModel() {
                // nothing
            }

            public static final String table = "QRY_IMPORT_MODEL";
        }

        public static class ImportRecord {

            private ImportRecord() {
                // nothing
            }

            public static final String table = "QRY_IMPORT_RECORD";
        }

        public static class ImportRecordItem {

            private ImportRecordItem() {
                // nothing
            }

            public static final String table = "QRY_IMPORT_RECORD_ITEM";
        }
    }

    public static class Stat {

        private Stat() {
            // nothing
        }

        public static final String table = "QRY_STAT";
    }

    public static class Reveal {

        private Reveal() {
            // nothing
        }

        public static final String table = "QRY_REVEAL";
    }

    public static class Item {

        private Item() {
            // nothing
        }

        public static final String table = "QRY_ITEM";
    }

    public static class Schema {

        private Schema() {
            // nothing
        }

        public static class Table {

            private Table() {
                // nothing
            }

            public static final String table = "QRY_SCH_TABLE";
        }

        public static class Statement {

            private Statement() {
                // nothing
            }

            public static final String TABLE = "QRY_SCH_STATEMENT";
        }

        public static class ExternalConnection {

            private ExternalConnection() {
                // nothing
            }

            public static final String TABLE = "QRY_SCH_EXTERNALCONNECTION";
        }

    }

    public static class Neural {

        private Neural() {
            // nothing
        }

        public static class Model {

            private Model() {
                // nothing
            }

            public static final String table = "QRY_NRL_MODEL";
        }

        public static class OutValue {

            private OutValue() {
                // nothing
            }

            public static final String table = "QRY_NRL_OUTVALUE";
        }

        public static class InValue {

            private InValue() {
                // nothing
            }

            public static final String table = "QRY_NRL_INVALUE";
        }

        public static class Entry {

            private Entry() {
                // nothing
            }

            public static final String table = "QRY_NRL_ENTRY";
        }

        public static class InText {

            private InText() {
                // nothing
            }

            public static final String table = "QRY_NRL_INTEXT";
        }

        public static class OutText {

            private OutText() {
                // nothing
            }

            public static final String table = "QRY_NRL_OUTTEXT";
        }

    }

    public static class Index {

        private Index() {
            // nothing
        }

        public static class State {

            private State() {
                // nothing
            }

            public static final String TABLE = "QRY_IDX_STATE";
        }

    }

}
