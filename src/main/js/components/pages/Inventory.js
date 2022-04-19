import React from "react";
import { connect } from "react-redux";

import { useItemInInventory } from '../../stores/inventorySlice';

const mapStateToProps = state => {
    return { inventory: state.inventory };
};

class Inventory extends React.Component {
    useItem(id) {
        this.props.dispatch(useItemInInventory(id));
    }

    render() {
        return (
            <div>
                <h2>Inventory</h2>
                <ul>
                    {
                        this.props.inventory.loaded ?
                        this.props.inventory.items.map((item) => (
                            <li key={item.id}>{item.item.name} - {item.item.category} - {item.id} <button onClick={()=>this.useItem(item.id)}>USE</button></li>
                        )) 
                        :
                        "loading..."
                    }
                </ul>
            </div>
        )
    }
}

export default connect(mapStateToProps, null)(Inventory);