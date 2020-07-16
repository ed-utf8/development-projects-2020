<?php

define ('DB_USER', 'bot');
define ('DB_PASSWORD', 'JNopN2YcL5LMKYnr');
define ('DB_HOST', '35.187.124.51');
define ('DB_NAME', 'playlive');

$connection = @mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME)
OR die('Could not connect to MySQL ' .
    mysqli_connect_error());