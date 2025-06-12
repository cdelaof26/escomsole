package modeling.parser.statements;

public interface Statement {
     public <T> T accept(VisitorStatement<T> visitor);
}
