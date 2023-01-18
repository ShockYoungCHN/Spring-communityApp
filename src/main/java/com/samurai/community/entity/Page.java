package com.samurai.community.entity;

public class Page {
    // Current page number
    private int current = 1;
    // upper limit of displaying entities per page
    private int limit = 10;
    // Total number of data (used to calculate the total number of pages)
    private int rows;
    // Query path (for reusing paging links)
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if(current >= 1){
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if(limit >= 1 && limit <= 100){
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if(rows >= 0){
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Get the starting row of the current page
     * @return the starting row of the current page
     */
    public int getOffset(){
        // current * limit - limit
        return (current - 1) * limit;
    }

    /**
     * Get the total number of pages
     * @return the total number of pages
     */
    public int getTotal(){
        //return rows % limit == 0 ? rows / limit : rows / limit + 1;
        return (rows+limit-1)/limit;
    }

    /**
     * Get the start of displaying page range
     * @return the start page number
     */
    public int getFrom(){
        int from = current - 2;
        return Math.max(from, 1);
    }

    /**
     * Get the end of displaying page range
     * @return the end page number
     */
    public int getTo(){
        int to = current + 2;
        int total = getTotal();
        return Math.min(to, total);
    }

}
