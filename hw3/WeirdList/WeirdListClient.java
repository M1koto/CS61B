/** Functions to increment and sum the elements of a WeirdList.
 */
class WeirdListClient {

    /** Return the result of adding N to each element of L. */
    static WeirdList add(WeirdList L, int n) {
        Adding function = new Adding(n);
        return L.map(function);
    }

    /** Return the sum of all the elements in L. */
    static int sum(WeirdList L) {
        summing function = new summing();
        L.map(function);
        return function.getSum();
    }

    /* IMPORTANT: YOU ARE NOT ALLOWED TO USE RECURSION IN ADD AND SUM
     *
     * As with WeirdList, you'll need to add an additional class or
     * perhaps more for WeirdListClient to work. Again, you may put
     * those classes either inside WeirdListClient as private static
     * classes, or in their own separate files.

     * You are still forbidden to use any of the following:
     *       if, switch, while, for, do, try, or the ?: operator.
     *
     * HINT: Try checking out the IntUnaryFunction interface.
     *       Can we use it somehow?
     */
}
    class Adding implements IntUnaryFunction {
        private int number;
        public Adding(int n) {
            this.number = n;
        }

        @Override
        public int apply(int x) {
            return this.number + x;
        }
    }

    class summing implements IntUnaryFunction {
    private int sum;
        public summing() {
            this.sum = 0;
        }

        @Override
        public int apply(int x) {
            this.sum += x;
            return this.sum;
        }
        public int getSum() {
            return this.sum;
        }
    }

