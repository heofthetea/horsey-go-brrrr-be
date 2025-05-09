package brrrr.go.horsey.orm;

import brrrr.go.horsey.service.JEN;
import jakarta.persistence.AttributeConverter;

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
