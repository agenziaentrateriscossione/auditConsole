export default class AuditChange {
  field: string;
  keys: object;
  before: string;
  after: string;

  static deserialize(jsonChange: any): AuditChange {
    const auditChange = new AuditChange();
    auditChange.field = jsonChange.field;
    auditChange.keys = jsonChange.keys;
    auditChange.before = jsonChange.before;
    auditChange.after = jsonChange.after;
    return auditChange;
  }
}
