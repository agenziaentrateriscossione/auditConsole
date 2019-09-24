export default class RemoteUser {
  username: string;
  remoteAddress: string;

  static deserialize(userJson: any): RemoteUser {
    const remoteUser = new RemoteUser();
    remoteUser.username = userJson.username;
    remoteUser.remoteAddress = userJson.remoteAddress;
    return remoteUser;
  }
}
