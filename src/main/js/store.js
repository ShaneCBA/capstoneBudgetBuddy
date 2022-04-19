import { configureStore, combineReducers } from '@reduxjs/toolkit';

import shopSlice from './stores/shopSlice';
import userSlice from './stores/userSlice';
import petSlice from './stores/petSlice';
import inventorySlice from './stores/inventorySlice';

const store = configureStore({
    reducer: combineReducers({
        user: userSlice,
        shop: shopSlice,
        pet: petSlice,
        inventory: inventorySlice
    })
});


export default store;