import { environment } from './../../environments/environment';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ParamMap } from '@angular/router';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { map } from 'rxjs/operators';
import { CookieService } from 'ngx-cookie-service';

import { AppSettings, AuthSettings, UserSettings } from '../settings';

@Injectable()
export class AuthService {

  private token: string;

  constructor(private http: HttpClient, private cookieService: CookieService) {}

  public isAuthenticated(): boolean {
    return (localStorage.getItem(AuthSettings.JWT_TOKEN_LOCALSTORE_KEY)
        && JSON.parse(localStorage.getItem(UserSettings.CURRENT_USER_LOCALSTORE_KEY)));
  }

  public login(username: string, password: string, urlParams: ParamMap): Observable<boolean> {
    let params = '';
    if (urlParams) {
      for (const entry of urlParams.keys) {
        if (entry) {
          params += ((params === '') ? '?' : '&') + entry + '=' + urlParams.get(entry);
        }
      }
    }
    return this.http
      .post<any>(
                AppSettings.LOGIN_ENDPOINT + params,
                JSON.stringify({ 'username' : username, 'password': password }),
                { observe: 'response' }
      )
      .pipe(
        map((response: HttpResponse<any>) => {
          // login successful if there's a jwt token in the response
          const pageResponse = response.body;
          // console.log(pageResponse);
          return this.storeUserData(response);
        })
      );
  }

  private storeUserData(response: HttpResponse<any>): boolean {
    let stored = false;
    const pageResponse = response.body;
    // console.log(pageResponse);
    if (pageResponse) {
      const token = response.headers.get(AuthSettings.AUTHORIZATION_HEADER_NAME);
      if (token) {
        localStorage.setItem(AuthSettings.JWT_TOKEN_LOCALSTORE_KEY, token);

        // current user
        const currentUserJson = {
          login: pageResponse.login,
          username: pageResponse.username
        };
        localStorage.setItem(UserSettings.CURRENT_USER_LOCALSTORE_KEY, JSON.stringify(currentUserJson));

        // Aggiunta del token JWT anche a livello di cookie
        this.cookieService.set(
          AuthSettings.AUTHORIZATION_COOKIE_NAME,
          token,
          1,
          environment.href_base_url + '/',
          document.location.hostname,
          false
        );

        // app url mapper
        localStorage.setItem(AppSettings.APP_URL_MAPPER_LOCALSTORE_KEY, JSON.stringify(pageResponse['app-url-mapper']));

        // app limits
        localStorage.setItem(AppSettings.APP_SETTINGS_LOCALSTORE_KEY, JSON.stringify(pageResponse['app-settings']));

        stored = true;
      }
    }
    return stored;
  }

  public loginByAuthKey(authkey: string): Observable<boolean> {
    return this.http
      .get<any>(
                AppSettings.API_ENDPOINT + '/xwaylogin?authkey=' + encodeURIComponent(authkey),
                { observe: 'response' }
      )
      .pipe(
        map((response: HttpResponse<any>) => {
          // login successful if there's a jwt token in the response
          return this.storeUserData(response);
        })
      );
  }

  public logout(): void {
    // clear token remove user from local storage to log user out
    localStorage.removeItem(UserSettings.CURRENT_USER_LOCALSTORE_KEY);
    localStorage.removeItem(AuthSettings.JWT_TOKEN_LOCALSTORE_KEY);
    localStorage.removeItem(AppSettings.APP_URL_MAPPER_LOCALSTORE_KEY);
    localStorage.removeItem(AppSettings.APP_SETTINGS_LOCALSTORE_KEY);

    this.cookieService.deleteAll();
  }

}
