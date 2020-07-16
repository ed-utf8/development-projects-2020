<?php
/**
 * Created by PhpStorm.
 * User: Edward
 * Date: 27/04/2017
 * Time: 19:35
 */

use PayPal\Api\Payment;
use PayPal\Api\PaymentExecution;
use ElephantIO\Client;
use ElephantIO\Engine\SocketIO\Version1X;

require 'app/start.php';

if (!isset($_GET['success'], $_GET['paymentId'], $_GET['PayerID'], $_SESSION['hash'])) {
    header("Location: ../index.php");
    die();
}

if ((bool) $_GET['success'] === false) {
    header("Location: ../index.php");
    die();
}

$paymentId = $_GET['paymentId'];
$payerId = $_GET['PayerID'];

$payment = Payment::get($paymentId, $paypal);

$execute = new PaymentExecution();
$execute->setPayerId($payerId);

try {
    $payment->execute($execute, $paypal);

    $hash = $_SESSION['hash'];

    $SQL = "UPDATE transactions_paypal SET complete = '1' WHERE hash = '$hash'";
    $SQL = @mysqli_query($connection, $SQL);

    $SQL = "SELECT * FROM transactions_paypal WHERE hash = '$hash' AND complete = '1' ORDER BY id ASC LIMIT 1;";
    $SQL = @mysqli_query($connection, $SQL);
    $SQL = mysqli_fetch_array($SQL);

    $streamer = $SQL['streamer_id'];
    $item = $SQL['item_id'];
    $category = $SQL['category_id'];

    $elephant = new Client(new Version1X('http://localhost:3000'));


    $elephant->initialize();
    $elephant->emit('message', ['Streamer' => $streamer, 'Category' => $category, 'Item' => $item]);
    $elephant->close();

} catch (Exception $e) {
    $data = json_decode($e->getMessage());
    echo $data->message;
    die();
}

header("Location: ../thanks.php");