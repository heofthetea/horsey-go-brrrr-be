package brrrr.go.horsey.orm;

import brrrr.go.horsey.service.JEN;
import jakarta.persistence.AttributeConverter;

/**
 * Custom Converter to map a {@link JEN} object to a strin in the database,
 * and vice versa.
 */
public class JENConverter implements AttributeConverter<JEN, String> {

    @Override
    public String convertToDatabaseColumn(JEN jen) {
        return jen.toString();
    }

    @Override
    public JEN convertToEntityAttribute(String dbAttribute) {
        return new JEN(dbAttribute);
    }
}
