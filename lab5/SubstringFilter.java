/**
 * TableFilter to filter for containing substrings.
 *
 * @author Matthew Owen
 */
public class SubstringFilter extends TableFilter {

    public SubstringFilter(Table input, String colName, String subStr) {
        super(input);
        // FIXME: Add your code here.
        _table = input;
        _name = colName;
        _match = subStr;
    }

    @Override
    protected boolean keep() {
        // FIXME: Replace this line with your code.
        int data = _table.colNameToIndex(_name);
        if (!candidateNext().getValue(data).contains(_match)){
            return false;
        }
        return true;
    }

    // FIXME: Add instance variables?
    private Table _table;
    private String _name;

    private String _match;
}
