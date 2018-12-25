
package pl.biotronika.blueblood.json.gsonpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Terapia {

    @SerializedName("nid")
    @Expose
    private List<Nid> nid = null;
    @SerializedName("uuid")
    @Expose
    private List<Uuid> uuid = null;
    @SerializedName("vid")
    @Expose
    private List<Vid> vid = null;
    @SerializedName("langcode")
    @Expose
    private List<Langcode> langcode = null;
    @SerializedName("type")
    @Expose
    private List<Type> type = null;
    @SerializedName("revision_timestamp")
    @Expose
    private List<RevisionTimestamp> revisionTimestamp = null;
    @SerializedName("revision_uid")
    @Expose
    private List<RevisionUid> revisionUid = null;
    @SerializedName("revision_log")
    @Expose
    private List<Object> revisionLog = null;
    @SerializedName("status")
    @Expose
    private List<Status> status = null;
    @SerializedName("title")
    @Expose
    private List<Title> title = null;
    @SerializedName("uid")
    @Expose
    private List<Uid> uid = null;
    @SerializedName("created")
    @Expose
    private List<Created> created = null;
    @SerializedName("changed")
    @Expose
    private List<Changed> changed = null;
    @SerializedName("promote")
    @Expose
    private List<Promote> promote = null;
    @SerializedName("sticky")
    @Expose
    private List<Sticky> sticky = null;
    @SerializedName("default_langcode")
    @Expose
    private List<DefaultLangcode> defaultLangcode = null;
    @SerializedName("revision_translation_affected")
    @Expose
    private List<RevisionTranslationAffected> revisionTranslationAffected = null;
    @SerializedName("path")
    @Expose
    private List<Path> path = null;
    @SerializedName("content_translation_source")
    @Expose
    private List<ContentTranslationSource> contentTranslationSource = null;
    @SerializedName("content_translation_outdated")
    @Expose
    private List<ContentTranslationOutdated> contentTranslationOutdated = null;
    @SerializedName("body")
    @Expose
    private List<Body> body = null;
    @SerializedName("field_biozap_buttons")
    @Expose
    private List<FieldBiozapButton> fieldBiozapButtons = null;
    @SerializedName("field_dni_do_potwierdzenia_skute")
    @Expose
    private List<FieldDniDoPotwierdzeniaSkute> fieldDniDoPotwierdzeniaSkute = null;
    @SerializedName("field_skrypt")
    @Expose
    private List<FieldSkrypt> fieldSkrypt = null;
    @SerializedName("field_urzadzenie")
    @Expose
    private List<FieldUrzadzenie> fieldUrzadzenie = null;

    public List<Nid> getNid() {
        return nid;
    }

    public void setNid(List<Nid> nid) {
        this.nid = nid;
    }

    public List<Uuid> getUuid() {
        return uuid;
    }

    public void setUuid(List<Uuid> uuid) {
        this.uuid = uuid;
    }

    public List<Vid> getVid() {
        return vid;
    }

    public void setVid(List<Vid> vid) {
        this.vid = vid;
    }

    public List<Langcode> getLangcode() {
        return langcode;
    }

    public void setLangcode(List<Langcode> langcode) {
        this.langcode = langcode;
    }

    public List<Type> getType() {
        return type;
    }

    public void setType(List<Type> type) {
        this.type = type;
    }

    public List<RevisionTimestamp> getRevisionTimestamp() {
        return revisionTimestamp;
    }

    public void setRevisionTimestamp(List<RevisionTimestamp> revisionTimestamp) {
        this.revisionTimestamp = revisionTimestamp;
    }

    public List<RevisionUid> getRevisionUid() {
        return revisionUid;
    }

    public void setRevisionUid(List<RevisionUid> revisionUid) {
        this.revisionUid = revisionUid;
    }

    public List<Object> getRevisionLog() {
        return revisionLog;
    }

    public void setRevisionLog(List<Object> revisionLog) {
        this.revisionLog = revisionLog;
    }

    public List<Status> getStatus() {
        return status;
    }

    public void setStatus(List<Status> status) {
        this.status = status;
    }

    public List<Title> getTitle() {
        return title;
    }

    public void setTitle(List<Title> title) {
        this.title = title;
    }

    public List<Uid> getUid() {
        return uid;
    }

    public void setUid(List<Uid> uid) {
        this.uid = uid;
    }

    public List<Created> getCreated() {
        return created;
    }

    public void setCreated(List<Created> created) {
        this.created = created;
    }

    public List<Changed> getChanged() {
        return changed;
    }

    public void setChanged(List<Changed> changed) {
        this.changed = changed;
    }

    public List<Promote> getPromote() {
        return promote;
    }

    public void setPromote(List<Promote> promote) {
        this.promote = promote;
    }

    public List<Sticky> getSticky() {
        return sticky;
    }

    public void setSticky(List<Sticky> sticky) {
        this.sticky = sticky;
    }

    public List<DefaultLangcode> getDefaultLangcode() {
        return defaultLangcode;
    }

    public void setDefaultLangcode(List<DefaultLangcode> defaultLangcode) {
        this.defaultLangcode = defaultLangcode;
    }

    public List<RevisionTranslationAffected> getRevisionTranslationAffected() {
        return revisionTranslationAffected;
    }

    public void setRevisionTranslationAffected(List<RevisionTranslationAffected> revisionTranslationAffected) {
        this.revisionTranslationAffected = revisionTranslationAffected;
    }

    public List<Path> getPath() {
        return path;
    }

    public void setPath(List<Path> path) {
        this.path = path;
    }

    public List<ContentTranslationSource> getContentTranslationSource() {
        return contentTranslationSource;
    }

    public void setContentTranslationSource(List<ContentTranslationSource> contentTranslationSource) {
        this.contentTranslationSource = contentTranslationSource;
    }

    public List<ContentTranslationOutdated> getContentTranslationOutdated() {
        return contentTranslationOutdated;
    }

    public void setContentTranslationOutdated(List<ContentTranslationOutdated> contentTranslationOutdated) {
        this.contentTranslationOutdated = contentTranslationOutdated;
    }

    public List<Body> getBody() {
        return body;
    }

    public void setBody(List<Body> body) {
        this.body = body;
    }

    public List<FieldBiozapButton> getFieldBiozapButtons() {
        return fieldBiozapButtons;
    }

    public void setFieldBiozapButtons(List<FieldBiozapButton> fieldBiozapButtons) {
        this.fieldBiozapButtons = fieldBiozapButtons;
    }

    public List<FieldDniDoPotwierdzeniaSkute> getFieldDniDoPotwierdzeniaSkute() {
        return fieldDniDoPotwierdzeniaSkute;
    }

    public void setFieldDniDoPotwierdzeniaSkute(List<FieldDniDoPotwierdzeniaSkute> fieldDniDoPotwierdzeniaSkute) {
        this.fieldDniDoPotwierdzeniaSkute = fieldDniDoPotwierdzeniaSkute;
    }

    public List<FieldSkrypt> getFieldSkrypt() {
        return fieldSkrypt;
    }

    public void setFieldSkrypt(List<FieldSkrypt> fieldSkrypt) {
        this.fieldSkrypt = fieldSkrypt;
    }

    public List<FieldUrzadzenie> getFieldUrzadzenie() {
        return fieldUrzadzenie;
    }

    public void setFieldUrzadzenie(List<FieldUrzadzenie> fieldUrzadzenie) {
        this.fieldUrzadzenie = fieldUrzadzenie;
    }

}
