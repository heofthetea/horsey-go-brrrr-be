package brrrr.go.horsey.rest;

import brrrr.go.horsey.service.JEN;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;

/**
 * JSON serializer for a {@link JEN} object.
 */
public class JENSerializer extends StdSerializer<JEN> {
    public JENSerializer() {
        this(null);
    }

    public JENSerializer(Class<JEN> t) {
        super(t);
    }

    @Override
    public void serialize(JEN jen, JsonGenerator gen, SerializerProvider provider)  throws IOException {
        gen.writeString(jen.toString());
    }
}
