package dynamite.graphdb.ast;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class VertexInfo {

    public final String id;
    public final List<String> labels;

    public VertexInfo(String id) {
        this(id, null);
    }

    public VertexInfo(String id, final List<String> labels) {
        this.id = id;
        this.labels = labels == null ? null : Collections.unmodifiableList(labels);
    }

    @Override
    public String toString() {
        return String.format("VInfo(%s, %s)", id, labels);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof VertexInfo)) return false;
        VertexInfo o = (VertexInfo) obj;
        return Objects.equals(id, o.id);
    }

}
