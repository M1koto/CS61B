/**
 * TableFilter to filter for entries whose two columns match.
 *
 * @author Matthew Owen
 */
public class ColumnMatchFilter extends TableFilter {

    public ColumnMatchFilter(Table input, String colName1, String colName2) {
        super(input);
        // FIXME: Add your code here.
        _table = input;
        _col1 = colName1;
        _col2 = colName2;
    }

    @Override
    protected boolean keep() {
        // FIXME: Replace this line with your code.
        int one = _table.colNameToIndex(_col1);
        int two = _table.colNameToIndex(_col2);
        Table.TableRow _next = candidateNext();
        if (_next.getValue(one).equals( _next.getValue(two))) {
            return true;
        }
        return false;
    }

    // FIXME: Add instance variables?
    public Table _table;
    public String _col1;

    public String _col2;
}
