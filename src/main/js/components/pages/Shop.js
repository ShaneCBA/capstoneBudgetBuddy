import React from "react";
import { connect } from "react-redux";
import Modal from 'react-modal';

import { userBuyItem } from '../../stores/userSlice';

const mapStateToProps = state => {
    return { shop: state.shop };
};

class Inventory extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            error: "",
            showModal: false
        }
    }

    buyItem(itemId) {
        this.props.dispatch(userBuyItem(itemId));
    }

    closeModal =()=>{
        this.setState({showModal: false})
    }

    render() {
        return (
            <div>
                <h2>SHOP</h2>
                {
                    this.props.shop.loaded ?
                    <ul>
                        {this.props.shop.items.map(item=>(
                            <li key={item.id}>
                                {item.name} - {item.category} - {item.cost} points <button onClick={()=>this.buyItem(item.id)}>BUY</button>
                            </li>
                        ))}
                    </ul>
                    :
                    "loading..."
                }
                <Modal
                isOpen={this.state.showModal}
                onRequestClose={this.closeModal}
                shouldCloseOnOverlayClick={true}
                >
                    {this.state.error}
                    <button onClick={this.closeModal}>Close</button>
                </Modal>
            </div>
        )
    }
}

export default connect(mapStateToProps, null)(Inventory)