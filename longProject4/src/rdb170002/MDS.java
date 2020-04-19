/**
 * Starter code for MDS
 *
 * @author rbk
 */

// Change to your net id
package rdb170002;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

// If you want to create additional classes, place them in this file as subclasses of MDS

public class MDS {
	
	private class Product
	{
		private List<Long> productDescription;
		private Money productPrice;

		public Product(Money price, List<Long> description) 
		{
			this.productPrice = price;
			this.productDescription = new LinkedList<>(description);
		}

		public Money getProductPrice() 
		{
			return productPrice;
		}

		public void setProductPrice(Money price)
		{
			this.productPrice = price;
		}	

		public List<Long> getProductDescription() 
		{
			return productDescription;
		}

		public void setProductDescription(List<Long> description) 
		{
			this.productDescription = new LinkedList<Long>(description);
		}			
	}
	
    // Add fields of MDS here
	HashMap<Long, TreeSet<Long>> productsDescription;
	TreeMap<Long, Product> products;
	
    // Constructors
    public MDS() {
    	products = new TreeMap<>();
		productsDescription = new HashMap<>();
    }

    /* Public methods of MDS. Do not change their signatures.
       __________________________________________________________________
       a. Insert(id,price,list): insert a new item whose description is given
       in the list.  If an entry with the same id already exists, then its
       description and price are replaced by the new values, unless list
       is null or empty, in which case, just the price is updated. 
       Returns 1 if the item is new, and 0 otherwise.
    */
    public int insert(long id, Money price, java.util.List<Long> list) {
		List<Long> distinctDescriptions = list.stream().distinct().collect(Collectors.toList());
		list = distinctDescriptions;

		Product item = products.get(id);

		boolean isAdded = false;
		if(item == null)
		{
			isAdded = true;
			item = new Product(price,list);
			products.put(id, item);
		} 
		else 
		{
			item.setProductPrice(price);

			//Retrieve the description list
			List<Long> descriptionsTemp = item.getProductDescription();						
			if (list != null && !list.isEmpty())
			{
				item.setProductDescription(list);
				deleteProductDescription(id, descriptionsTemp);
			}
		}

		// Updating the ids
		for (Long descriptions : list) 
		{
			TreeSet<Long> descriptionTreeSet = productsDescription.get(descriptions);
			if (descriptionTreeSet == null)
			{
				descriptionTreeSet = new TreeSet<Long>();
				descriptionTreeSet.add(id);
				productsDescription.put(descriptions, descriptionTreeSet);
			} 
			else
			{
				descriptionTreeSet.add(id);
			}
		}
		return (isAdded) ? 1 : 0;
    }
    
    private long deleteProductDescription(long id, List<Long> descriptions) 
	{
		long totalSum = 0;

		for (Long d : descriptions)
		{ 
			TreeSet<Long> descriptionTreeSet = productsDescription.get(d);
			totalSum += d;
			if (descriptionTreeSet != null)
			{
				descriptionTreeSet.remove(id);
				//remove the entry if no ids are present
				if (descriptionTreeSet.size() == 0)
				{
					productsDescription.remove(d);
				}
			}
		}
		return totalSum;
	}

    // b. Find(id): return price of item with given id (or 0, if not found).
    public Money find(long id) {
    	Product productTemp = products.get(id);
		if (productTemp == null)
		{
			return new Money();
		}
		else
		{
			return productTemp.getProductPrice();
		}
    }

    /* 
       c. Delete(id): delete item from storage.  Returns the sum of the
       long ints that are in the description of the item deleted,
       or 0, if such an id did not exist.
    */
    public long delete(long id) {
		long sum = 0;
    	Product product = products.get(id);
		if (product != null)
		{
			List<Long> description = product.getProductDescription();
			sum = deleteProductDescription(id, description);
			products.remove(id);
		}
		return sum;
    }

    /* 
       d. FindMinPrice(n): given a long int, find items whose description
       contains that number (exact match with one of the long ints in the
       item's description), and return lowest price of those items.
       Return 0 if there is no such item.
    */
    public Money findMinPrice(long n) {
		Money productPrice;
		Money minProductPrice = new Money(Long.MAX_VALUE + "");
    	TreeSet<Long> descriptionTreeSet = productsDescription.get(n);

		if(descriptionTreeSet == null) 
		{
			return new Money();
		}
		else 
		{
			for(Long description : descriptionTreeSet) 
			{
				Product product = products.get(description);
				productPrice = product.getProductPrice();

				if(productPrice.compareTo(minProductPrice) <= 0)
				{
					minProductPrice = productPrice;
				}
			}
		}
		return minProductPrice;
    }

    /* 
       e. FindMaxPrice(n): given a long int, find items whose description
       contains that number, and return highest price of those items.
       Return 0 if there is no such item.
    */
    public Money findMaxPrice(long n) {
		Money productPrice;
		Money maxProductPrice = new Money(Long.MIN_VALUE + "");
    	TreeSet<Long> descriptionTreeSet = productsDescription.get(n);

		if(descriptionTreeSet == null) 
		{
			return new Money();
		}
		else 
		{
			for(Long description : descriptionTreeSet) 
			{
				Product product = products.get(description);
				productPrice = product.getProductPrice();

				if(productPrice.compareTo(maxProductPrice) >= 0)
				{
					maxProductPrice = productPrice;
				}
			}
		}
		return maxProductPrice;
    }

    /* 
       f. FindPriceRange(n,low,high): given a long int n, find the number
       of items whose description contains n, and in addition,
       their prices fall within the given range, [low, high].
    */
    public int findPriceRange(long n, Money low, Money high) {
		TreeSet<Long> sameProductDescription = productsDescription.get(n);
    	int priceRange = 0;

		// check if the price of the product id with given description falls in the range [low, high]
		for (Long description : sameProductDescription) 
		{
			Money productPrice = products.get(description).productPrice;
			if (productPrice.compareTo(low) >= 0 && productPrice.compareTo(high) <= 0) 
				priceRange += 1;
		}
		return priceRange;
    }

    /* 
       g. PriceHike(l,h,r): increase the price of every product, whose id is
       in the range [l,h] by r%.  Discard any fractional pennies in the new
       prices of items.  Returns the sum of the net increases of the prices.
    */
    public Money priceHike(long l, long h, double rate) {
		Money newPriceHike = new Money();
    	Money productPrice;
		Money newProductPrice = new Money();
		long hike = 0;
		long priceHike = 0;
		long hikeTotal = 0;
		BigDecimal newRate = BigDecimal.valueOf(rate).divide(BigDecimal.valueOf(100));

		for(Long productId : products.keySet()) 
		{
			if(productId >= l && productId <= h) 
			{
				Product product = products.get(productId);

				productPrice = product.getProductPrice();
				long price = Money.getMoney(productPrice);

				hike = newRate.multiply(BigDecimal.valueOf(price)).longValue();
				priceHike = price + hike;
				hikeTotal += hike;
				
				newProductPrice = Money.putMoney(priceHike);
				product.setProductPrice(newProductPrice);
			}
		}		
		newPriceHike = Money.putMoney(hikeTotal);
		return newPriceHike;
    }

    /*
      h. RemoveNames(id, list): Remove elements of list from the description of id.
      It is possible that some of the items in the list are not in the
      id's description.  Return the sum of the numbers that are actually
      deleted from the description of id.  Return 0 if there is no such id.
    */
    public long removeNames(long id, java.util.List<Long> list) {
    	long sumDeletedDescription = 0;
		if (list != null) 
		{
			for (Long description : list) 
			{
				TreeSet<Long> descriptionTreeSet = productsDescription.get(description);
				if (descriptionTreeSet != null) 
				{
					if (descriptionTreeSet.contains(id)) 
					{
						sumDeletedDescription = sumDeletedDescription + description;
						descriptionTreeSet.remove(id);

						Product prod = products.get(id);
						prod.productDescription.remove(description);
					}
					
					if (descriptionTreeSet.size() == 0) 
					{
						productsDescription.remove(description);
					}
				}
			}
		}
		return sumDeletedDescription;
    }

    // Do not modify the Money class in a way that breaks LP4Driver.java
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
            } else if (len == 1) {
                d = Long.parseLong(s);
                c = 0;
            } else {
                d = Long.parseLong(part[0]);
                c = Integer.parseInt(part[1]);
                /*if (part[1].length() == 1) {
                    c = c * 10;
                }*/
            }
        }

        public long dollars() {
            return d;
        }

        public int cents() {
            return c;
        }

        public int compareTo(Money other) { // Complete this, if needed
        	if (this.d > other.d)
				return 1;
			else if (this.d < other.d)
				return -1;
			else 
			{
				if (this.c > other.c)
					return 1;
				else if (this.c < other.c)
					return -1;
				else 
					return 0;
			}
        }

        public String toString() {
        	if (c <= 10)
			{
				return d + ".0" + c;
			}				
			else
			{
				return d + "." + c;	
			}
        }
        
        public static long getMoney(Money money)
		{
			long m = money.d * 100;
			return m + money.c;
		}
        
		public static Money putMoney(long price) throws NumberFormatException 
		{		
			long dollars = price/100;
			int cents = (int) (price - (dollars * 100));
			return new Money(dollars, cents);
		}
    }

}
