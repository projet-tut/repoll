package repoll.models;

import java.io.Serializable;

public abstract class DomainObject implements Serializable {
    public final static long UNSAVED_OBJECT_ID = -1;

    private long id = UNSAVED_OBJECT_ID;

    public final boolean isSaved() {
        return id != UNSAVED_OBJECT_ID;
    }

    public final long getId() {
        return id;
    }

    /*
     * Setter for primary key should be called only once - from corresponding mapper
     */
    public final void setId(long id) {
        if (isSaved()) {
            throw new AssertionError("setId() called twice for object " + this);
        }
        this.id = id;
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof DomainObject)) return false;
        return isSaved() && id == ((DomainObject) obj).getId();
    }
}