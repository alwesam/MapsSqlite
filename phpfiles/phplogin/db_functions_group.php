<?php
 
class DB_Functions {
 
    private $db; 
    
    // constructor
    function __construct() {
        include_once './db_connect_group.php';
        // connecting to database
        $this->db = new DB_Connect();
        $this->db->connect();
    }
 
    // destructor
    function __destruct() {
 
    }
 
    //add functions here
    
}
 
?>