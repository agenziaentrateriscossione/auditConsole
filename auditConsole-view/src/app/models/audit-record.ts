import User from './user';
import AuditChange from './audit-change';

export class AuditRecord {
  id: string;
  archivio: string;
  idRecord: string;
  tipoRecord: string;
  data: string;
  tipoAzione: string[];
  extraInfo: object;
  checksum: string;
  validChecksum: boolean;
  user: User;
  changes: AuditChange[];

  static deserialize(jsonRecord: any): AuditRecord {
    const auditRecord = new AuditRecord();
    auditRecord.id = jsonRecord.id;
    auditRecord.archivio = jsonRecord.archivio;
    auditRecord.idRecord = jsonRecord.idRecord;
    auditRecord.tipoRecord = jsonRecord.tipoRecord;
    auditRecord.data = jsonRecord.data;
    auditRecord.tipoAzione = jsonRecord.tipoAzione;
    auditRecord.extraInfo = jsonRecord.extraInfo;
    auditRecord.checksum = jsonRecord.checksum;
    auditRecord.validChecksum = jsonRecord.validChecksum;
    auditRecord.user = User.deserialize(jsonRecord.user);
    auditRecord.changes = AuditRecord.deserializeAllChanges(jsonRecord.changes);
    return auditRecord;
  }

  private static deserializeAllChanges(changesArray: any): AuditChange[] {
    const auditChanges: AuditChange[] = [];
    if (changesArray) {
      for (const change of changesArray) {
        auditChanges.push(AuditChange.deserialize(change));
      }
    }
    return auditChanges;
  }
}

export class PositionInfo {
  hasPrev: boolean;
  hasNext: boolean;

  constructor(hasPrev?: boolean, hasNext?: boolean) {
    this.hasPrev = hasPrev;
    this.hasNext = hasNext;
  }
}

