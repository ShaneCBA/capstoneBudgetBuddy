//provide tools to work with the pet
import { createSlice, createAsyncThunk } from '@reduxjs/toolkit'

import { fetchFromAPI } from '../apiCalls';

import { useItemInInventory } from './inventorySlice'

export const updatePet = createAsyncThunk('pet/updatePet', async (obj, {dispatch, getState, rejectWithValue, fulfillWithValue}) => {
    const response = await fetchFromAPI('petInfo');
    if (response.error) {
        return rejectWithValue(response.error);
    }
    return response.pet;
});

export const petSlice = createSlice({
    name: "pet",
    initialState: {
        loaded: false,
        pet: null
    },
    reducers: {

    },
    extraReducers: builder => {
        builder
            .addCase(updatePet.pending, (state) => {
                state.loaded = false;
            })
            .addCase(updatePet.fulfilled, (state, action) => {
                state.loaded = true;
                state.pet = action.payload;
            })
            .addCase(useItemInInventory.fulfilled, (state, action) => {
                state.pet = action.payload.pet;
            })
    }
})

// export const {} = petSlice.actions

export default petSlice.reducer