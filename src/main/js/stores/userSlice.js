//Should house user, points, and goal
import { createSlice, createAsyncThunk } from '@reduxjs/toolkit'

import { fetchFromAPI, fetchBuyItem } from '../apiCalls';

export const updateUser = createAsyncThunk('user/updateUser', async (obj, {dispatch, getState, rejectWithValue, fulfillWithValue}) => {
    const response = await fetchFromAPI('userInfo');
    if (response.error) {
        return rejectWithValue(response.error);
    }
    return response.user;
})

export const userBuyItem = createAsyncThunk('user/buyItem', async (id, {dispatch, getState, rejectWithValue, fulfillWithValue}) => {
    const response = await fetchBuyItem(id);
    if (response.error) {
        return rejectWithValue(response.error);
    }
    return response
})

export const userSlice = createSlice({
    name: "user",
    initialState: {
        loaded: false,
        user: null
    },
    reducers: {
        buyItem: (state, action) => {
            state.user.points -= action.payload.item.cost
        }
    },
    extraReducers: builder => {
        builder
            .addCase(updateUser.pending, (state) => {
                state.loaded = false;
            })
            .addCase(updateUser.fulfilled, (state, action) => {
                state.loaded = true;
                state.user = action.payload;
            })
            .addCase(userBuyItem.fulfilled, (state, action) => {
                state.user.points = action.payload.points;
            })
    }
})


export const {buyItem} = userSlice.actions

export default userSlice.reducer