const express = require("express");
const Stripe = require("stripe");
const cors = require("cors");
const bodyParser = require("body-parser");

const app = express();
app.use(cors());
app.use(bodyParser.json());

const stripe = Stripe("sk_test_51SOHDGHFc48fE60VwEoZR8bkl10R1JrpoyfQgRGTRFykAlGPQalztj3tjKqhZzk1hg47hPicHvBKG5iamohBRKf300S5tH95UX");

// Middleware untuk log setiap request masuk
app.use((req, res, next) => {
  console.log(`➡️  ${req.method} ${req.url} - Body:`, req.body);
  next();
});

app.post("/create-payment-intent", async (req, res) => {
  try {
    // Hardcode amount: RM100 = 10000 sen
    const amount = 10000;
    console.log("💰 Creating payment intent for amount:", amount);

    const paymentIntent = await stripe.paymentIntents.create({
      amount,
      currency: "myr",
      automatic_payment_methods: { enabled: true },
    });

    console.log("✅ PaymentIntent created:", paymentIntent.id);
    res.send({ clientSecret: paymentIntent.client_secret });
  } catch (error) {
    console.error("❌ Error creating PaymentIntent:", error.message);
    res.status(400).send({ error: { message: error.message } });
  }
});

app.listen(4242, "0.0.0.0", () => console.log("✅ Server running on port 4242"));
