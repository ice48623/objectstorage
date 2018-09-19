package com.muic.objectstorage.DTO;

public class FileUploadResponse {

    public static class normal {
        private String md5;
        private Integer length;
        private Integer partNumber;

        public normal(String md5, Integer length, Integer partNumber) {
            this.md5 = md5;
            this.length = length;
            this.partNumber = partNumber;
        }

        public String getMd5() {
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }

        public Integer getLength() {
            return length;
        }

        public void setLength(Integer length) {
            this.length = length;
        }

        public Integer getPartNumber() {
            return partNumber;
        }

        public void setPartNumber(Integer partNumber) {
            this.partNumber = partNumber;
        }
    }

    public static class withError {
        private String md5;
        private Integer length;
        private Integer partNumber;
        private String error;

        public withError(String md5, Integer length, Integer partNumber, String error) {
            this.md5 = md5;
            this.length = length;
            this.partNumber = partNumber;
            this.error = error;
        }

        public String getMd5() {
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }

        public Integer getLength() {
            return length;
        }

        public void setLength(Integer length) {
            this.length = length;
        }

        public Integer getPartNumber() {
            return partNumber;
        }

        public void setPartNumber(Integer partNumber) {
            this.partNumber = partNumber;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }
}
