const express = require('express');
const router = express.Router();
const User = require('../models/User');
const auth = require('../middleware/auth');
const jwt = require('jsonwebtoken');

// Register new user
router.post('/register', async (req, res) => {
    try {
        const user = new User(req.body);
        await user.save();
        
        const token = jwt.sign(
            { userId: user._id },
            process.env.JWT_SECRET || 'your-secret-key',
            { expiresIn: '7d' }
        );

        res.status(201).json({ user, token });
    } catch (error) {
        res.status(400).json({ error: error.message });
    }
});

// Login user
router.post('/login', async (req, res) => {
    try {
        const { email, password } = req.body;
        const user = await User.findOne({ email });

        if (!user || !(await user.comparePassword(password))) {
            throw new Error('Invalid login credentials');
        }

        const token = jwt.sign(
            { userId: user._id },
            process.env.JWT_SECRET || 'your-secret-key',
            { expiresIn: '7d' }
        );

        res.json({ user, token });
    } catch (error) {
        res.status(401).json({ error: error.message });
    }
});

// Get user profile
router.get('/profile', auth, async (req, res) => {
    res.json(req.user);
});

// Update user profile
router.patch('/profile', auth, async (req, res) => {
    const updates = Object.keys(req.body);
    const allowedUpdates = ['name', 'email', 'password', 'phone', 'address', 'fcmToken'];
    const isValidOperation = updates.every(update => allowedUpdates.includes(update));

    if (!isValidOperation) {
        return res.status(400).json({ error: 'Invalid updates!' });
    }

    try {
        updates.forEach(update => req.user[update] = req.body[update]);
        await req.user.save();
        res.json(req.user);
    } catch (error) {
        res.status(400).json({ error: error.message });
    }
});

// Delete user account
router.delete('/profile', auth, async (req, res) => {
    try {
        req.user.active = false;
        await req.user.save();
        res.json({ message: 'Account deactivated successfully' });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

module.exports = router;
