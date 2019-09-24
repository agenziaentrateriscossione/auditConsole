import User from './user';
import { stringify } from '@angular/compiler/src/util';

export default class AuditTitle {
  id: string;
  archivio: string;
  idRecord: string;
  tipoRecord: string;
  data: string;
  tipoAzione: string[];
  user: User;

  static deserialize(jsonTitle: any): AuditTitle {
    const auditTitle = new AuditTitle();
    auditTitle.id = jsonTitle.id;
    auditTitle.archivio = jsonTitle.archivio;
    auditTitle.idRecord = jsonTitle.idRecord;
    auditTitle.tipoRecord = jsonTitle.tipoRecord;
    auditTitle.data = jsonTitle.data;
    auditTitle.tipoAzione = jsonTitle.tipoAzione;
    auditTitle.user = User.deserialize(jsonTitle.user);
    return auditTitle;
  }
}
