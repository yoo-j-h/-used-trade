
import { Link } from "react-router-dom";
import styled from "styled-components";

export const InputPage = styled.div`
    height : 100%;
    display : flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
    padding-bottom: 120px;
`

export const InputContainer = styled.div`

    margin : 40px;
    padding: 15px 30px;
    background-color: white;
    display : flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
    border-radius: 10px;
    min-width: 400px;
    font-weight: 350;
`


export const TextLinks = styled(Link)`
    margin : 8px;
    color: #333;
    text-decoration: none;
    &:hover{
        color: #fd8b20ff;
    }
`
export const LeftAlign = styled.div`
    width : 100%;
    display: block;
    text-align: left;
`

export const InputMainText = styled.div`
    color : #fd8b20ff;
    font-size : 24px;
    font-weight: 450;
    margin: 8px 0;
`
export const InputBox = styled.div`
    text-align: left;
    width : 100%;
    padding : 0;
    margin: 5px 0;
`

export const InputBar = styled.input`
    width : 100%;

    padding: 12px;
    border: 1px solid #e2e2e2;
    outline: none;
    border-radius: 6px;

    &:focus{
        border-color: #fd8b20ff;
    }
`
export const InputButton = styled.button`
    width : 100%;
    margin : 14px 0;
    padding: 12px 24px;
    border: none;
    color: #ffffff;
    background: #fd8b20ff;
    border-radius: 6px;
    cursor: pointer;

    &:hover{
        color: #db791dff;
        scale: 1.02;
    }
`

export const  HorizontalRule = styled.hr`
    width : 100%;
    border: none;
    border-top: 0.5px solid #000;
    margin: 8px;
`
 