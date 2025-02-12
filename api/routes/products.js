const express = require('express');
const router = express.Router();
const Product = require('../models/Product');
const auth = require('../middleware/auth');

// Get all products with filters
router.get('/', async (req, res) => {
    try {
        const query = { active: true };
        const { category, minPrice, maxPrice, search } = req.query;

        if (category) {
            query.category = category;
        }

        if (minPrice || maxPrice) {
            query.price = {};
            if (minPrice) query.price.$gte = Number(minPrice);
            if (maxPrice) query.price.$lte = Number(maxPrice);
        }

        if (search) {
            query.$or = [
                { name: { $regex: search, $options: 'i' } },
                { description: { $regex: search, $options: 'i' } }
            ];
        }

        const products = await Product.find(query)
            .populate('category', 'name')
            .sort({ createdAt: -1 });

        res.json(products);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// Get product by ID
router.get('/:id', async (req, res) => {
    try {
        const product = await Product.findOne({ _id: req.params.id, active: true })
            .populate('category', 'name');
            
        if (!product) {
            return res.status(404).json({ error: 'Product not found' });
        }
        res.json(product);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// Get products by category
router.get('/category/:categoryId', async (req, res) => {
    try {
        const products = await Product.find({
            category: req.params.categoryId,
            active: true
        }).populate('category', 'name');
        res.json(products);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// Create new product (admin only)
router.post('/', auth, async (req, res) => {
    try {
        const product = new Product(req.body);
        await product.save();
        res.status(201).json(product);
    } catch (error) {
        res.status(400).json({ error: error.message });
    }
});

// Update product (admin only)
router.patch('/:id', auth, async (req, res) => {
    const updates = Object.keys(req.body);
    const allowedUpdates = [
        'name', 'description', 'price', 'imageUrl',
        'category', 'stock', 'discount', 'active'
    ];
    const isValidOperation = updates.every(update => allowedUpdates.includes(update));

    if (!isValidOperation) {
        return res.status(400).json({ error: 'Invalid updates!' });
    }

    try {
        const product = await Product.findById(req.params.id);
        if (!product) {
            return res.status(404).json({ error: 'Product not found' });
        }

        updates.forEach(update => product[update] = req.body[update]);
        await product.save();
        res.json(product);
    } catch (error) {
        res.status(400).json({ error: error.message });
    }
});

// Delete product (admin only)
router.delete('/:id', auth, async (req, res) => {
    try {
        const product = await Product.findById(req.params.id);
        if (!product) {
            return res.status(404).json({ error: 'Product not found' });
        }

        product.active = false;
        await product.save();
        res.json({ message: 'Product deleted successfully' });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

module.exports = router;
