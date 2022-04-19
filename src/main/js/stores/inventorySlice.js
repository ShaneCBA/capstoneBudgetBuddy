//Should simply just give tools for working with the inventory
import { createSlice, createAsyncThunk } from '@reduxjs/toolkit'

import { fetchFromAPI } from '../apiCalls';

import { userBuyItem } from './userSlice';

export const updateInventory = createAsyncThunk('inventory/updateInventory', async (obj, {dispatch, getState, rejectWithValue, fulfillWithValue}) => {
    const response = await fetchFromAPI('getInventory');
    if (response.error) {
        return rejectWithValue(response.error);
    }
    return response.inventory;
})

export const useItemInInventory = createAsyncThunk('inventory/useItem', async (id, {dispatch, getState, rejectWithValue, fulfillWithValue}) => {
    const response = await fetchFromAPI('useItem', {id});
    if (response.error) {
        return rejectWithValue(response.error);
    }
    return response;
})

export const inventorySlice = createSlice({
    name: "inventory",
    initialState: {
        loaded: false,
        items: []
    },
    reducers: {
        addItem: (state, action) => {
            state.items.push(action.payload.item);
        },
        useItem: (state, action) => {
            state.items[action.payload.id];
        }
    },
    extraReducers: builder => {
        builder
            .addCase(updateInventory.pending, (state) => {
                state.loaded = false;
            })
            .addCase(updateInventory.fulfilled, (state, action) => {
                state.loaded = true;
                state.items = action.payload;
            })
            .addCase(userBuyItem.fulfilled, (state, action) => {
                state.items.add(action.payload.item);
            })
            .addCase(useItemInInventory.fulfilled, (state, action) => {
                state.items = action.payload.inventory;
            })
    }
})

export const {addItem, useItem} = inventorySlice.actions

export default inventorySlice.reducer;
