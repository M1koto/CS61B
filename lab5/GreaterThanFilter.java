/**
 * TableFilter to filter for entries greater than a given string.
 *
 * @author Matthew Owen
 */
public class GreaterThanFilter extends TableFilter {

    public GreaterThanFilter(Table input, String colName, String ref) {
        super(input);
        // FIXME: Add your code here.
        _table = input;
        _name = colName;
        _ref = ref;
    }

    @Override
    protected boolean keep() {
        // FIXME: Replace this line with your code.
        int data = _table.colNameToIndex(_name);
        if (candidateNext().getValue(data).compareTo(_ref) <= 0) {
            return false;
        }
        return true;
}

    // FIXME: Add instance variables?
    private Table _table;
    private String _name;

    private String _ref;
}
