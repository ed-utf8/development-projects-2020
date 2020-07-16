<?php

if (!isset($_GET['category'], $_GET['product'], $_GET['streamer'])) {
    die();
}

include 'checkout.php';
?>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
        <title>PlayLive - Order</title>
        <?php include '../includes/header.php'; ?>
    </head>
    <body>
        <div class="container-fluid margintop">
            <div class="row">
                <div class="col-md-12">
                    <h2>Order</h2>
                    <form method="POST" action="checkout.php" class="form-horizontal">
                        <fieldset>
                            <!-- Text input-->
                            <div class="form-group">
                                <label class="col-md-4 control-label" for="IGN">Username</label>
                                <div class="col-md-4">
                                    <input id="IGN" name="IGN" type="text" placeholder="Steve" class="form-control input-md" required="">
                                    <span class="help-block">Minecraft IGN</span>
                                </div>
                            </div>
                            <!-- Textarea -->
                            <div class="form-group">
                                <label class="col-md-4 control-label" for="message">Message</label>
                                <div class="col-md-4">
                                    <textarea class="form-control" id="message" name="message" placeholder="Enter your message here..."></textarea>
                                </div>
                            </div>
                            <!-- Multiple Checkboxes (inline) -->
                            <div class="form-group">
                                <label class="col-md-4 control-label" for="tc">Terms &amp; Conditions</label>
                                <div class="col-md-4">
                                    <label class="checkbox-inline" for="tc-0">
                                        <input type="checkbox" name="tc" id="tc-0" value="1">
                                        I agree to <a data-toggle="modal" data-target="#myModal">these</a> Terms and Conditions
                                    </label>
                                </div>
                            </div>
                            <!-- Button -->
                            <div class="form-group">
                                <label  class="col-md-4 control-label" for="submit"></label>
                                <div class="col-md-4 buttonwidth">
                                    <button id="submit" name="submit" class="btn btn-primary">Submit</button>
                                </div>
                            </div>
                        </fieldset>
                    </form>
                </div>
            </div>
        </div>
        <!-- Modal -->
        <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                        <h4 class="modal-title" id="myModalLabel">Terms and Conditions</h4>
                    </div>
                    <div class="modal-body">
                        <p>By making a donation you agree to the following rules:</p>
                        <br/>
                        <p>1. I, the customer, am also, paying for virtual goods. I understand that absolutely each sale is final. The initial time of the product's life is determined by the vendor, and I cannot/will not dispute any payments (the punishment is an immediate permanent ban from our services). This payment is final, and won't be disputed. I agree to these terms for this product.</p>
                        <br/>
                        <p>2. All rules, commands, and prices are subject to change at any moment.</p>
                        <br/>
                        <p>3. By donating, I, the purchaser agree that I am 18 else have the bill payers permission to pay for these virtual goods. I, the purchaser, thus agree not to chargeback as all payments for this charitable donation are final.<br/>
                        <p>If found abusing these additional benefits, depending on the severity, a warning, demotion, or ban will be issued, as all of our payments are final, no refund will be given.</p>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                    </div>
                </div>
            </div>
        </div>
    </body>
    <?php include '../includes/footer.php'; ?>
</html>