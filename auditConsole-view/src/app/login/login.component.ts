import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import { Component, OnInit } from '@angular/core';

import { AuthService } from '../services/auth.service';
import { faUserSecret } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-login',
  templateUrl: 'login.component.html',
  styleUrls: ['login.component.css']
})

export class LoginComponent implements OnInit {

  model: any = {};
  errorMessage = '';

  authkey: string;

  faUserSecret = faUserSecret;

  constructor(private router: Router, private activatedRoute: ActivatedRoute, private authService: AuthService) {
  }

  ngOnInit() {
    // reset login status
    this.authService.logout();

    this.activatedRoute.queryParamMap.subscribe((params: ParamMap) => {
      this.authkey = params.get('authkey');
      // console.log('authkey = ' + this.authkey);
      if (this.authkey) {
        this.authService.loginByAuthKey(this.authkey)
        .subscribe(
            result => {
              if (result === true) {
                this._loadHomePage();
              } else {
                this._printError();
              }
            },
            error => {
              console.error('Si è verificato un errore durante il progesso di login... status = '
                            + ((error.status) ? error.status : 'undefined'));
              this._printError();
            }
        );
      }
    });
  }

  login() {
    this.authService.login(this.model.username, this.model.password, null)
      .subscribe(
          result => {
            if (result === true) {
              this._loadHomePage();
            } else {
              this._printError();
            }
          },
          error => {
            console.error('Si è verificato un errore durante il progesso di login... status = '
                            + ((error.status) ? error.status : 'undefined'));
            this._printError();
          }
      );
  }

  private _loadHomePage(): void {
    this.router.navigate(['/']);
  }

  private _printError(): void {
    this.errorMessage = 'Username o password non corretti';
    this.authkey = null;
  }

}
