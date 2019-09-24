export class AppSettings {
  public static APP_NAME = 'Audit Console';
  public static API_ENDPOINT = 'api';
  public static LOGIN_ENDPOINT = AppSettings.API_ENDPOINT + '/login';
  public static ELEMENT_PER_PAGE = 20;
  public static BOOTSTRAP_MINOR_MD_BREAKPOINT = 767.98;
  public static APP_URL_MAPPER_LOCALSTORE_KEY = 'auditAppUrlMapper';

  public static APP_SETTINGS_LOCALSTORE_KEY = 'auditSettings';
  public static EXPORT_MAX_SIZE_KEY = 'export-max-size';
  public static EXPORT_VALIDATION_MAX_SIZE_KEY = 'export-validation-max-size';
  public static HIDE_IP_ADDRESS = 'hide-ip-address';
}
export class UserSettings {
  public static CURRENT_USER_LOCALSTORE_KEY = 'auditUser';
}
export class AuthSettings {
  public static AUTHORIZATION_HEADER_NAME = 'Authorization';
  public static AUTHORIZATION_COOKIE_NAME = 'AUDIT_COOKIE';
  public static JWT_TOKEN_LOCALSTORE_KEY = 'auditToken';
}
