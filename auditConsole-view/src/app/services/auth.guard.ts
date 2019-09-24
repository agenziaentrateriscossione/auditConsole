import { Injectable } from '@angular/core';
import { CanActivate } from '@angular/router';
import { Router } from '@angular/router';

import { UserSettings } from '../settings';

@Injectable()
export class AuthGuard implements CanActivate {
    constructor(private router: Router) {}

    canActivate() {
        if (localStorage.getItem(UserSettings.CURRENT_USER_LOCALSTORE_KEY)) {
            return true;
        }

        this.router.navigate(['/login']);
        return false;
    }
}
