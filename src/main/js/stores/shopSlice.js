//Provide tools to work with the shop
import { createSlice, createAsyncThunk } from '@reduxjs/toolkit'

import { fetchFromAPI } from '../apiCalls';

export const updateShop = createAsyncThunk('shop/updateShop', async (obj, {dispatch, getState, rejectWithValue, fulfillWithValue}) => {
    const response = await fetchFromAPI('storeItems');
    if (response.error) {
        return rejectWithValue(response.error);
    }
    return response.storeItems;
})

export const shopSlice = createSlice({
    name: "shop",
    initialState: {
        loaded: false,
        items: []
    },
    reducers: {
    },
    extraReducers: builder => {
        builder
            .addCase(updateShop.pending, (state) => {
                state.loaded = false;
            })
            .addCase(updateShop.fulfilled, (state, action) => {
                state.loaded = true;
                state.items = action.payload;
            })
    }
})

// export const {} = shopSlice.actions

export default shopSlice.reducer