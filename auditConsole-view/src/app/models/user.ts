export default class User {
  username: string;
  codUser: string;
  ipAddress: string;
  actualUsername: string;
  actualCodUser: string;
  delegaUsername: string;
  delegaCodUser: string;

  static deserialize(jsonUser: any) {
    const user = new User();
    user.username = jsonUser.username;
    user.codUser = jsonUser.codUser;
    user.ipAddress = jsonUser.ipAddress;
    user.actualUsername = jsonUser.actualUsername;
    user.actualCodUser = jsonUser.actualCodUser;
    user.delegaUsername = jsonUser.delegaUsername;
    user.delegaCodUser = jsonUser.delegaCodUser;
    return user;
  }
}
