import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, throwError } from 'rxjs';
import { tap } from 'rxjs/operators';
import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
  HttpResponse
} from '@angular/common/http';
import { LoadingSpinner } from '@tredi/widgets-tredi';
import { BsModalError } from '@tredi/widgets-bs';

import { AppSettings, AuthSettings } from '../settings';

// Guida: https://blog.hackages.io/angular-http-httpclient-same-but-different-86a50bbcc450
@Injectable()
export class AuditHttpInterceptor implements HttpInterceptor {

  constructor(
    private loadingSpinner: LoadingSpinner,
    private bsModalError: BsModalError,
    private router: Router) {
  }

  /**
   * Intercept an outgoing `HttpRequest` and optionally transform it or the
   * response.
   *
   * Typically an interceptor will transform the outgoing request before returning
   * `next.handle(transformedReq)`. An interceptor may choose to transform the
   * response event stream as well, by applying additional Rx operators on the stream
   * returned by `next.handle()`.
   *
   * More rarely, an interceptor may choose to completely handle the request itself,
   * and compose a new event stream instead of invoking `next.handle()`. This is
   * acceptable behavior, but keep in mind further interceptors will be skipped entirely.
   *
   * It is also rare but valid for an interceptor to return multiple responses on the
   * event stream for a single request.
   */
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const background = this.isBackground(req);
    if (!background) {
      this.showLoader();
    }

    return next.handle(this.addAuthorizationHeader(req))
      .pipe(
        tap((event: HttpEvent<any>) => {
          if (event instanceof HttpResponse) {
            // do stuff with response if you want
            if (!background) {
              this.hideLoader();
            }
          }
        }, (error: any) => {
          console.error(error);
          if (!background) {
            this.hideLoader();
          }

          if (req.url.startsWith(AppSettings.API_ENDPOINT + '/')) {
            return this.showErrorDetails(error);
          }
        }
      ));
  }

  private isBackground(req: HttpRequest<any>): Boolean {
    return (req.headers.get('background') === 'true');
  }

  /**
   * Aggiunta del token JWT di autenticazione all'header della richiesta HTTP
   * @param req
   */
  private addAuthorizationHeader(req: HttpRequest<any>): HttpRequest<any> {
    if (req.url.startsWith(AppSettings.API_ENDPOINT + '/')) {
      // richiesta verso API, richiede il token autoritativo
      const token = localStorage.getItem(AuthSettings.JWT_TOKEN_LOCALSTORE_KEY);
      if (token) {
        // console.log('Append token "' + token + '" to request header...');
        return req.clone({ setHeaders: { [AuthSettings.AUTHORIZATION_HEADER_NAME] : token } });
      }
    }
    return req;
  }

  /**
   * Visualizzazione dello spinner di attesa di caricamento
   */
  private showLoader(): void {
    this.loadingSpinner.show();
  }

  /**
   * Chiusura dello spinner di attesa di caricamento
   */
  private hideLoader(): void {
    this.loadingSpinner.hide();
  }

  /**
   * Visualizzazione dei dettagli di un eventuale errore ricevuto dalla chiamata HTTP
   * @param err
   */
  private showErrorDetails(err: HttpErrorResponse): Observable<any> {
    // console.log(err.url);
    if (err && err.url.indexOf(AppSettings.LOGIN_ENDPOINT) === -1) { // errore da intercettare solo se non si tratta di un login
      let jsonbody;
      try {
        if (err.error) {
          if (typeof(err.error) === 'string') {
            jsonbody = { 'message': err.error };
          } else {
            jsonbody = err.error;
          }
        }
      } catch (e) {
        console.error(e);
        jsonbody = {
          'message': 'Unable to parse response body',
          'description': 'Possibile problema di connessione con il server', 'level': 'FATAL', 'stacktrace': e
        };
      }
      this.bsModalError.show(err.status, jsonbody, () => {
        if (err.status === 401 || err.status === 403 || jsonbody.level === 'FATAL') {
          this.router.navigate(['login']);
        }
      });
    }
    return throwError(err);
  }

}
