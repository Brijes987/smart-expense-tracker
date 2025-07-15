const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
const rateLimit = require('express-rate-limit');
const NodeCache = require('node-cache');
const swaggerJsdoc = require('swagger-jsdoc');
const swaggerUi = require('swagger-ui-express');
const axios = require('axios');

const app = express();
const PORT = process.env.PORT || 3000;

// Cache for 5 minutes
const cache = new NodeCache({ stdTTL: 300 });

// Middleware
app.use(helmet());
app.use(cors());
app.use(express.json());

// Rate limiting
const limiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 100 // limit each IP to 100 requests per windowMs
});
app.use(limiter);

// Swagger configuration
const swaggerOptions = {
  definition: {
    openapi: '3.0.0',
    info: {
      title: 'Currency Conversion Service',
      version: '1.0.0',
      description: 'Microservice for currency conversion in expense tracker'
    },
    servers: [
      {
        url: `http://localhost:${PORT}`,
        description: 'Development server'
      }
    ]
  },
  apis: ['./server.js']
};

const specs = swaggerJsdoc(swaggerOptions);
app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(specs));

/**
 * @swagger
 * /api/currency/convert:
 *   post:
 *     summary: Convert currency amount
 *     tags: [Currency]
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               amount:
 *                 type: number
 *               fromCurrency:
 *                 type: string
 *               toCurrency:
 *                 type: string
 *     responses:
 *       200:
 *         description: Conversion successful
 */
app.post('/api/currency/convert', async (req, res) => {
  try {
    const { amount, fromCurrency, toCurrency } = req.body;
    
    if (!amount || !fromCurrency || !toCurrency) {
      return res.status(400).json({ error: 'Missing required parameters' });
    }
    
    const cacheKey = `${fromCurrency}_${toCurrency}`;
    let exchangeRate = cache.get(cacheKey);
    
    if (!exchangeRate) {
      // Using a free API for demo purposes
      // In production, use a reliable service like XE, CurrencyLayer, etc.
      const response = await axios.get(
        `https://api.exchangerate-api.com/v4/latest/${fromCurrency}`
      );
      
      exchangeRate = response.data.rates[toCurrency];
      if (!exchangeRate) {
        return res.status(400).json({ error: 'Invalid currency code' });
      }
      
      cache.set(cacheKey, exchangeRate);
    }
    
    const convertedAmount = amount * exchangeRate;
    
    res.json({
      originalAmount: amount,
      fromCurrency,
      toCurrency,
      exchangeRate,
      convertedAmount: Math.round(convertedAmount * 100) / 100
    });
    
  } catch (error) {
    console.error('Currency conversion error:', error);
    res.status(500).json({ error: 'Internal server error' });
  }
});

/**
 * @swagger
 * /api/currency/rates/{currency}:
 *   get:
 *     summary: Get exchange rates for a currency
 *     tags: [Currency]
 *     parameters:
 *       - in: path
 *         name: currency
 *         required: true
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: Exchange rates retrieved successfully
 */
app.get('/api/currency/rates/:currency', async (req, res) => {
  try {
    const { currency } = req.params;
    const cacheKey = `rates_${currency}`;
    
    let rates = cache.get(cacheKey);
    
    if (!rates) {
      const response = await axios.get(
        `https://api.exchangerate-api.com/v4/latest/${currency}`
      );
      rates = response.data.rates;
      cache.set(cacheKey, rates);
    }
    
    res.json({
      baseCurrency: currency,
      rates
    });
    
  } catch (error) {
    console.error('Exchange rates error:', error);
    res.status(500).json({ error: 'Internal server error' });
  }
});

/**
 * @swagger
 * /health:
 *   get:
 *     summary: Health check
 *     responses:
 *       200:
 *         description: Service is healthy
 */
app.get('/health', (req, res) => {
  res.json({ status: 'Currency Service is running!' });
});

app.listen(PORT, () => {
  console.log(`Currency Service running on port ${PORT}`);
  console.log(`Swagger docs available at http://localhost:${PORT}/api-docs`);
});