import RemoteUser from '../models/remote-user';
import { UserSettings } from '../settings';

export default class RemoteUserUtils {

  /**
   * Ritorna l'utente corrente (recuperandolo dal local storage)
   */
  static getCurrentUser(): RemoteUser {
    // TODO evitare caricamento ad ogni invocazione dell'header?
    let remoteUser: RemoteUser;
    const currentUser = localStorage.getItem(UserSettings.CURRENT_USER_LOCALSTORE_KEY);
    if (currentUser) {
      remoteUser = RemoteUser.deserialize(JSON.parse(currentUser));
    }
    return remoteUser;
  }
}
