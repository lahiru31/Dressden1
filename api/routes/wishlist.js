const express = require('express');
const router = express.Router();
const Wishlist = require('../models/Wishlist');
const auth = require('../middleware/auth');

// Get user's wishlist
router.get('/', auth, async (req, res) => {
    try {
        const wishlist = await Wishlist.findOne({ user: req.user._id })
            .populate({
                path: 'products.product',
                select: 'name price imageUrl discount',
                match: { active: true }
            });

        if (!wishlist) {
            return res.json({ products: [] });
        }

        // Filter out any null products (inactive products)
        wishlist.products = wishlist.products.filter(item => item.product != null);
        res.json(wishlist);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// Add product to wishlist
router.post('/:productId', auth, async (req, res) => {
    try {
        let wishlist = await Wishlist.findOne({ user: req.user._id });

        if (!wishlist) {
            wishlist = new Wishlist({
                user: req.user._id,
                products: []
            });
        }

        // Check if product already exists in wishlist
        const productExists = wishlist.products.some(
            item => item.product.toString() === req.params.productId
        );

        if (productExists) {
            return res.status(400).json({ error: 'Product already in wishlist' });
        }

        wishlist.products.push({
            product: req.params.productId,
            addedAt: new Date()
        });

        await wishlist.save();
        res.status(201).json(wishlist);
    } catch (error) {
        res.status(400).json({ error: error.message });
    }
});

// Remove product from wishlist
router.delete('/:productId', auth, async (req, res) => {
    try {
        const wishlist = await Wishlist.findOne({ user: req.user._id });

        if (!wishlist) {
            return res.status(404).json({ error: 'Wishlist not found' });
        }

        wishlist.products = wishlist.products.filter(
            item => item.product.toString() !== req.params.productId
        );

        await wishlist.save();
        res.json(wishlist);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// Clear wishlist
router.delete('/', auth, async (req, res) => {
    try {
        const wishlist = await Wishlist.findOne({ user: req.user._id });

        if (!wishlist) {
            return res.status(404).json({ error: 'Wishlist not found' });
        }

        wishlist.products = [];
        await wishlist.save();
        res.json({ message: 'Wishlist cleared successfully' });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

module.exports = router;
