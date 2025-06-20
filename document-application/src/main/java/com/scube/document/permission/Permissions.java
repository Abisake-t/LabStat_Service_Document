package com.scube.document.permission;

/**
 * Generated DO NOT MODIFY!
 * date: 2024-07-17T17:58:43.024015900Z
 */
public class Permissions {
    private Permissions() {
    }

    /**
     * Generated DO NOT MODIFY!
     * date: 2024-07-17T17:58:43.024015900Z
     */
    public static class Document {
        public static final String HANDLE_FILE_UPLOAD = "document-service--handle-file-upload";

        public static final String HANDLE_FILE_UPDATE = "document-service--handle-file-update";

        public static final String GET_FILE = "document-service--get-file";

        public static final String GET_METADATA = "document-service--get-metadata";

        public static final String DELETE = "document-service--delete";

        public static final String GET_HISTORY = "document-service--get-history";

        public static final String DOWNLOAD_ZIP = "document-service--download-zip";

        private Document() {
        }
    }

    /**
     * Generated DO NOT MODIFY!
     * date: 2024-07-17T17:58:43.039024Z
     */
    public static class LoggedInUserDocument {
        public static final String GET_FILE_PREVIEW = "document-service-me-get-file-preview";

        public static final String GET_FILE = "document-service-me-get-file";

        public static final String GET_METADATA = "document-service-me-get-metadata";

        public static final String GET_HISTORY = "document-service-me-get-history";

        private LoggedInUserDocument() {
        }
    }

    /**
     * Generated DO NOT MODIFY!
     * date: 2024-07-17T17:58:43.039024Z
     */
    public static class Permission {
        public static final String SEED_ROLES_TO_ALL_REALMS = "document-service-permissions-seed-roles-to-all-realms";

        public static final String SEED_ROLES_BY_REALM = "document-service-permissions-seed-roles-by-realm";

        private Permission() {
        }
    }
}
