<?php
/**
 * User: Edward
 * Date: 27/04/2017
 * Time: 19:13
 */

use PayPal\Api\Payer;
use PayPal\Api\Item;
use PayPal\Api\Details;
use PayPal\Api\Amount;
use PayPal\Api\Transaction;
use PayPal\Api\ItemList;
use PayPal\Api\RedirectUrls;
use PayPal\Api\Payment;

require 'app/start.php';
include '../sql/dbc.php';

if (isset($_GET['category'], $_GET['product'], $_GET['streamer'], $_POST['IGN'], $_POST['message'], $_POST['submit'], $_POST['tc-0'])) {

    $category = $_GET['category'];
    $streamer = $_GET['streamer'];
    $product_id = $_GET['product'];
    $name = $_POST['IGN'];
    $message = $_POST['message'];

    $payer = new Payer();
    $payer->setPaymentMethod('paypal');

    $SQL = "SELECT * FROM items WHERE category_id = '$category' AND id = '$product_id';";
    $SQL = @mysqli_query($connection, $SQL);
    $SQL = mysqli_fetch_array($SQL);

    $name = $SQL['name'];
    $price = $SQL['price'];

    $item = new Item();
    $item->setName($name)
        ->setCurrency('USD')
        ->setQuantity(1)
        ->setPrice($price);

    $itemList = new ItemList();
    $itemList->setItems([$item]);

    $details = new Details();
    $details->setSubtotal($price);

    $amount = new Amount();
    $amount->setCurrency('USD')
        ->setDetails($details)
        ->setTotal($price);

    $transaction = new Transaction();
    $transaction->setAmount($amount)
        ->setItemList($itemList)
        ->setDescription("A donation to charity (PlayLive for St Jude's)")
        ->setInvoiceNumber(uniqid());

    $redirect = new RedirectUrls();
    $redirect->setReturnUrl(SITE_URL . '/pay.php?success=true')
        ->setCancelUrl(SITE_URL . '/pay.php?success=false');

    $payment = new Payment();
    $payment->setIntent('sale')
        ->setPayer($payer)
        ->setRedirectUrls($redirect)
        ->setTransactions([$transaction]);

    try {
        $payment->create($paypal);

        $paymentid = $payment->getId();
        $hash = md5($paymentid);

        $SQL = "INSERT INTO transactions_paypal VALUES ('$category', '$product_id', '$streamer', '$paymentid', '$hash', '$price');";
        $SQL = @mysqli_query($connection, $SQL);

        $_SESSION['hash'] = $hash;
    } catch (Exception $e) {
        die($e);
    }

    $approvalUrl = $payment->getApprovalLink();

    header("Location: {$approvalUrl}");
}