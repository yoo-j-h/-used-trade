import React from 'react'
import { useNavigate } from 'react-router-dom'
import { HorizontalRule, InputBar, InputBox, InputButton, InputContainer, InputMainText, InputPage, LeftAlign, TextLinks } from './Input.styled'

const SingupPage = () => {
    const navigate = useNavigate()

  return (
    <InputPage>
        <InputContainer>
            <LeftAlign>
                <TextLinks>← 돌아가기</TextLinks>
            </LeftAlign>
            
            <InputMainText>우동마켓</InputMainText>
            로그인하고 거래를 시작하세요.<br/><br/>
           <InputBox>
                이메일
                <InputBar/>
           </InputBox>
           <InputBox>
                이메일
                <InputBar/>
           </InputBox>
           <InputButton>회원가입</InputButton>
           <HorizontalRule/>
           <TextLinks>계정이 없으신가요?</TextLinks>
        </InputContainer>
    </InputPage>
  )
}

export default SingupPage