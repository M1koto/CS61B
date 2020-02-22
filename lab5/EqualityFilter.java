/**
 * TableFilter to filter for entries equal to a given string.
 *
 * @author Matthew Owen
 */
public class EqualityFilter extends TableFilter {

    public EqualityFilter(Table input, String colName, String match) {
        super(input);
        // FIXME: Add your code here.
        _table = input;
        _name = colName;
        _match = match;
    }

    @Override
    protected boolean keep() {
        // FIXME: Replace this line with your code.
        int data = _table.colNameToIndex(_name);
        if (!candidateNext().getValue(data).equals(_match)){
            return false;
        }
        return true;
    }

    // FIXME: Add instance variables?
    private Table _table;
    private String _name;
    private String _match;
}
