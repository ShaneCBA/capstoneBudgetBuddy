//Provide tools to work with the transaction
import { createSlice, createAsyncThunk } from '@reduxjs/toolkit'

import { fetchFromAPI } from '../apiCalls';

export const updateTransaction = createAsyncThunk('transaction/updateTransaction', async (obj, {dispatch, getState, rejectWithValue, fulfillWithValue}) => {
    const response = await fetchFromAPI('latestTransactions');
    if (response.error) {
        return rejectWithValue(response.error);
    }
    return response.storeItems;
})

export const transactionSlice = createSlice({
    name: "transaction",
    initialState: {
        loaded: false,
        items: []
    },
    reducers: {
    },
    extraReducers: builder => {
        builder
            .addCase(updateTransaction.pending, (state) => {
                state.loaded = false;
            })
            .addCase(updateTransaction.fulfilled, (state, action) => {
                state.loaded = true;
                state.items = action.payload;
            })
    }
})

// export const {} = transactionSlice.actions

export default transactionSlice.reducer