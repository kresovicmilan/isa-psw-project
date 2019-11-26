import React from 'react';

import doctorImage from '../../../assets/images/doctor.png';
import patientImage from '../../../assets/images/patient.png';
import nurseImage from '../../../assets/images/nurse.png';
import clinicalAdminImage from '../../../assets/images/clinicaladmin.png';
import clinicalCenterAdminImage from '../../../assets/images/clinicalcenteradmin.png';
import classes from './UserInfo.module.css';
import './UserInfo.css';

const UserInfo = (props) => {   
    let image

    if(props.role === 'patient'){
      image = patientImage;
    }
    else if (props.role === 'doctor') {
      image = doctorImage;
    }
    else if (props.role === 'nurse'){
      image = nurseImage;
    }
    else if (props.role === 'adminclinic'){
        image = clinicalAdminImage;
    }
    else if (props.role === 'adminclinicalcenter'){
        image = clinicalCenterAdminImage;
    }

    return (
                <div class={[classes.UserInfo, classes.Table].join(' ') + " col-xs-12 col-sm-12 col-md-6 col-lg-6 col-xs-offset-0 col-sm-offset-0 col-md-offset-3 col-lg-offset-3 toppad"} >
                    <div class="panel panel-info">
                        <div class="panel-heading">
                            <h4 class="panel-title">User profile</h4>
                        </div>
                        <div class="panel-body">
                            <div class="row">
                                <div class="col-md-4 col-lg-2" align='center' style={{marginTop: '20px'}}>
                                    <img alt="User Pic" src={image} class="img-circle img-responsive"/>
                                </div>
                                <div class=" col-md-9 col-lg-9 ">
                                    <table class="table table-user-information">
                                        <tbody>
                                            <tr>
                                                <td>Role:</td>
                                                <td>{props.role}</td>
                                            </tr>
                                            <tr>
                                                <td>Name:</td>
                                                <td>{props.name}</td>
                                            </tr>
                                            <tr>
                                                <td>Lastname:</td>
                                                <td>{props.lastname}</td>
                                            </tr>          
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
    );
};

export default UserInfo;
