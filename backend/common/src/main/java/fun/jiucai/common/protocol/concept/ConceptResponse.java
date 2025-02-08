package fun.jiucai.common.protocol.concept;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author godotg
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConceptResponse {
    private List<Concept> concepts;
    private String core;
}
