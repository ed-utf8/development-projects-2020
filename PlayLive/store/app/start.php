<?php

session_start();

require '../../vendor/autoload.php';

define('SITE_URL', 'https://www.ariesmc.net');

$paypal = new \PayPal\Rest\ApiContext(
    new \PayPal\Auth\OAuthTokenCredential(
        '',
        ''
    )
);