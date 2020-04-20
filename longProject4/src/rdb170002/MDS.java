/**
 * @author Ayesha Gurnani, Rutali Bandivadekar, Viraj Mavani
 */

package rdb170002;

import java.util.*;

public class MDS {

	//Map with id as key and entry as value
	private Map<Long, Entry> entryMap;
	
	//Map with map of map with description as key and map of price and price's counter as value
    private Map<Long, Map<Money, Integer>> setMap;


    private static class Entry implements Comparable {
        private Long id;
        private Money price;
		private Set<Long> description;
		
		/**
         * @param id          unique identifier of a product
         * @param price       price of the product
         * @param description description of a product
         */
        Entry(long id, Money price, Set<Long> description) {
            this.id = id;
            this.price = price;
            this.description = description;
        }

        @Override
        public int hashCode() {
            return Long.hashCode(this.id);
        }

        /**
         * @param o product to be compared
         * @return int value based upon comparision.
         * 1, if Object o's price is smaller than object's price
         * 0, if Object o's price is equal to the object's price
         * -1, if Object o's price is greater than object's price
         */
        @Override
        public int compareTo(Object o) {
            return this.price.compareTo(((Entry) o).price);
        }

        
        @Override
        public String toString() {
            return price.toString();
        }
    }

    /**
	 * Constructor of MDS 
	 */

    public MDS() {
        entryMap = new HashMap<>();
        setMap = new HashMap<>();
    }

	/**
	 * a. Insert a new item whose description is given
	 in the list.  If an entry with the same id already exists, then its
	 description and price are replaced by the new values, unless list
	 is null or empty, in which case, just the price is updated. 
	 Returns 1 if the item is new, and 0 otherwise.

	 * @param id unique identification of product
	 * @param price price of the product
	 * @param list description of the product
	 * @return 
	 */

    public int insert(long id, Money price, java.util.List<Long> list) {
        int result = entryMap.get(id) == null ? 1 : 0;

        Entry e = result == 0 ? entryMap.get(id) : new Entry(id, price, new HashSet<>(list));
        if (result == 0) {
            for (long x : e.description) {
                if (setMap.get(x).get(e.price) != null) {
                    setMap.get(x).put(e.price, setMap.get(x).get(e.price) - 1);
                    if (setMap.get(x).get(e.price) <= 0) setMap.get(x).remove(e.price);
                }
            }
            if (list != null && list.size() > 0) {
                e.description.clear();
                e.description.addAll(list);

            }
            e.price = price;
        }
        for (Long x : e.description) {
            if (setMap.get(x) == null) {
                setMap.put(x, new TreeMap<>());
            }
            if (setMap.get(x).get(e.price) == null) {
                setMap.get(x).put(e.price, 1);
            } else {
                setMap.get(x).put(e.price, setMap.get(x).get(e.price) + 1);
            }
        }
        entryMap.put(id, e);

        return result;
    }
	
	/**
	 * b. Find(id): return price of item with given id (or 0, if not found).
	 * @param id unique identification of product
	 * @return price of the product
	 */
	
    public Money find(long id) {
        Money money = entryMap.get(id) != null ? entryMap.get(id).price : new Money();
        return money;
    }

	/**
	 *  c. Delete(id): delete item from storage.  Returns the sum of the
       long ints that are in the description of the item deleted,
       or 0, if such an id did not exist.
	 * @param id unique identification of product
	 * @return sum of int of the description of product that is deleted
	 */

    public long delete(long id) {
        long sum = 0;
        if (entryMap.get(id) != null) {
            Entry e = entryMap.remove(id);
            for (long x : e.description) {
                sum += x;
                if (setMap.get(x).get(e.price) != null) {
                    setMap.get(x).put(e.price, setMap.get(x).get(e.price) - 1);
                    if (setMap.get(x).get(e.price) <= 0) setMap.get(x).remove(e.price);
                }
            }
        }
        return sum;
    }

	/**
	 *
       d. FindMinPrice(n): given a long int, find items whose description
       contains that number (exact match with one of the long ints in the
       item's description), and return lowest price of those items.
       Return 0 if there is no such item.
    
	 * @param n number for searching in description of all products
	 * @return lowest priced product
	 */

    public Money findMinPrice(long n) {
        Money money = new Money();
        TreeMap<Money, Integer> map = (TreeMap<Money, Integer>) setMap.get(n);
        if (map != null && map.size() > 0) {
            money = map.firstKey();
        }
        return money;
    }


    /**
    * e. FindMaxPrice(n): given a long int, find items whose description
      contains that number, and return highest price of those items.
      Return 0 if there is no such item.
     * @param n number for searching in description of all products
	 * @return maximum priced product
	 */

    public Money findMaxPrice(long n) {
        Money money = new Money();
        TreeMap<Money, Integer> map = (TreeMap<Money, Integer>) setMap.get(n);
        if (map != null && map.size() > 0) {
            money = map.lastKey();

        }
        return money;
    }

	/**
	 * f. FindPriceRange(n,low,high): given a long int n, find the number
       of items whose description contains n, and in addition,
       their prices fall within the given range, [low, high].
	 * @param n number for searching in description of all products
	 * @param low lower price of item to be found
	 * @param high higher price of item to be found
	 * @return
	 */
    public int findPriceRange(long n, Money low, Money high) {
        TreeMap<Money, Integer> map = (TreeMap<Money, Integer>) setMap.get(n);
        int i = 0;
        for (Money e : map.keySet()) {
            if (e.compareTo(low) >= 0 && e.compareTo(high) <= 0) {
                i += map.get(e);
            }
        }
        return i;
    }

	/**
	 *  g. PriceHike(l,h,r): increase the price of every product, whose id is
       in the range [l,h] by r%.  Discard any fractional pennies in the new
	   prices of items.  Returns the sum of the net increases of the prices.
	   
	 * @param l lower limit 
	 * @param h higher limit for unique idetification 
	 * @param rate rate at which price is increased
	 * @return sum of all price hike for the range given
	 */

    public Money priceHike(long l, long h, double rate) {
        long hike = 0;
        while (l <= h) {
            if (entryMap.get(l) != null) {
                Money oldPrice = entryMap.get(l).price;
                long op = (oldPrice.dollars() * 100) + oldPrice.cents();
                long np = Double.valueOf(op * (1 + (0.01 * rate))).longValue();
                long dollars = np / 100;
                int cents = Long.valueOf(np % 100).intValue();
                Money newPrice = new Money(dollars, cents);
                insert(entryMap.get(l).id, newPrice, new ArrayList<>());
                hike += (np - op);
            }

            l++;
        }
        return new Money(hike / 100, Long.valueOf(hike % 100).intValue());
    }

	/**
	 * h. RemoveNames(id, list): Remove elements of list from the description of id.
      It is possible that some of the items in the list are not in the
      id's description.  Return the sum of the numbers that are actually
	  deleted from the description of id.  Return 0 if there is no such id.
	  
	 * @param id unique identification of the product
	 * @param list item's list to be removed from product description
	 * @return sum of the numbers that are deleted from description of product
	 */

    public long removeNames(long id, java.util.List<Long> list) {
        Entry e = entryMap.get(id);
        long sum = 0;
        for (Long x : list) {
            if (e.description.remove(x)) {
                sum += x;
                setMap.get(x).put(e.price, setMap.get(x).get(e.price) - 1);
                if (setMap.get(x).get(e.price) <= 0) setMap.get(x).remove(e.price);
                if (setMap.get(x).keySet().size() == 0) setMap.remove(x);
            }
        }
        return sum;
    }

	/**
	 * Money class for representing money 
	 */
    public static class Money implements Comparable<Money> {
        long d;
        int c;

        public Money() {
            d = 0;
            c = 0;

        }

        public Money(long d, int c) {
            this.d = d;
            this.c = c;

		}
		
        public Money(String s) {
            String[] part = s.split("\\.");
            int len = part.length;
            if (len < 1) {
                d = 0;
                c = 0;
            } else if (part.length == 1) {
                d = Long.parseLong(s);
                c = 0;
            } else {
				d = Long.parseLong(part[0]);
				c = Integer.parseInt(part[1]);
                if (part[1].length() == 1) {
					c = c * 10;
				}
            }


        }

        public long dollars() {
            return d;
        }

        @Override
        public int hashCode() {
            return Double.hashCode(Double.valueOf(this.toString()));
        }

        public int cents() {
            return c;
        }
		
		/**
		 * Checks if money is equal or not
		 */
        @Override
        public boolean equals(Object obj) {
            return this.d == ((Money) obj).d && this.c == ((Money) obj).c;
        }


       /**
		* Compare method for money objects
	    */
        public int compareTo(Money other) {
            if (this.d == other.d && this.c == other.c) return 0;
            if (this.d == other.d) {
                if (this.c < other.c) return -1;
                return 1;
            }
            if (this.d < other.d) return -1;
            return 1;
		}
		
        public String toString() {
            String str = String.valueOf(c);
            return d + "." + str;
        }

    }

}
