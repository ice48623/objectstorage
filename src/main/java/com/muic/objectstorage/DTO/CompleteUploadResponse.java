package com.muic.objectstorage.DTO;

public class CompleteUploadResponse {

    public static class normal {
        private String eTag;
        private Integer length;
        private String name;

        public normal(String eTag, Integer length, String name) {
            this.eTag = eTag;
            this.length = length;
            this.name = name;
        }

        public String geteTag() {
            return eTag;
        }

        public void seteTag(String eTag) {
            this.eTag = eTag;
        }

        public Integer getLength() {
            return length;
        }

        public void setLength(Integer length) {
            this.length = length;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class withError {
        private String eTag;
        private Integer length;
        private String name;
        private String error;

        public withError(String eTag, Integer length, String name, String error) {
            this.eTag = eTag;
            this.length = length;
            this.name = name;
            this.error = error;
        }

        public String geteTag() {
            return eTag;
        }

        public void seteTag(String eTag) {
            this.eTag = eTag;
        }

        public Integer getLength() {
            return length;
        }

        public void setLength(Integer length) {
            this.length = length;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }
}
