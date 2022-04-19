import React from "react";
import { connect } from "react-redux";

const mapStateToProps = state => {
    return { petInfo: state.pet };
};

class PetInfo extends React.Component {
    render() {
        return (
            <div>
            <h2>Pet Information</h2>
            {
                this.props.petInfo.loaded ?
                <ul>
                    <li>Name: {this.props.petInfo.pet.name}</li>
                    <li>Happiness: {this.props.petInfo.pet.happiness}</li>
                    <li>Satiation: {this.props.petInfo.pet.satiation}</li>
                    <li>Condition: {this.props.petInfo.pet.condition}</li>
                </ul>
                :
                "loading..."
            }
            </div>
        )
    }
}

export default connect(mapStateToProps, null)(PetInfo)