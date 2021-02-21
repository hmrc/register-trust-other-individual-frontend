/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package utils.print

import com.google.inject.Inject
import controllers.register.individual.mld5.{routes => mld5irts}
import controllers.register.individual.{routes => irts}
import models.UserAnswers
import pages.register.individual._
import pages.register.individual.mld5._
import play.api.i18n.Messages
import viewmodels.{AnswerRow, AnswerSection}

class OtherIndividualPrintHelper @Inject()(answerRowConverter: AnswerRowConverter) {

  def printSection(userAnswers: UserAnswers, name: String, index: Int, draftId: String)
                  (implicit messages: Messages): AnswerSection = {
    AnswerSection(
      Some(Messages("answerPage.section.otherIndividual.subheading", index + 1)),
      answers(userAnswers, name, index, draftId)
    )
  }

  def checkDetailsSection(userAnswers: UserAnswers, name: String, index: Int, draftId: String)
                         (implicit messages: Messages): AnswerSection = {
    AnswerSection(
      None,
      answers(userAnswers, name, index, draftId)
    )
  }

  def answers(userAnswers: UserAnswers, name: String, index: Int, draftId: String)
             (implicit messages: Messages): Seq[AnswerRow] = {
    val bound: answerRowConverter.Bound = answerRowConverter.bind(userAnswers, name)

    Seq(
      bound.nameQuestion(NamePage(index), "otherIndividual.name", irts.NameController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(DateOfBirthYesNoPage(index), "otherIndividual.dateOfBirthYesNo", irts.DateOfBirthYesNoController.onPageLoad(index, draftId).url),
      bound.dateQuestion(DateOfBirthPage(index), "otherIndividual.dateOfBirth", irts.DateOfBirthController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(CountryOfNationalityYesNoPage(index), "otherIndividual.5mld.countryOfNationalityYesNo", mld5irts.CountryOfNationalityYesNoController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(CountryOfNationalityInTheUkYesNoPage(index), "otherIndividual.5mld.countryOfNationalityInTheUkYesNo", mld5irts.CountryOfNationalityInTheUkYesNoController.onPageLoad(index, draftId).url),
      bound.countryQuestion(CountryOfNationalityPage(index), CountryOfNationalityInTheUkYesNoPage(index), "otherIndividual.5mld.countryOfNationality",  mld5irts.CountryOfNationalityController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(NationalInsuranceYesNoPage(index), "otherIndividual.nationalInsuranceYesNo", irts.NationalInsuranceYesNoController.onPageLoad(index, draftId).url),
      bound.ninoQuestion(NationalInsuranceNumberPage(index), "otherIndividual.nationalInsuranceNumber", irts.NationalInsuranceNumberController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(CountryOfResidenceYesNoPage(index), "otherIndividual.5mld.countryOfResidenceYesNo", mld5irts.CountryOfResidenceYesNoController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(CountryOfResidenceInTheUkYesNoPage(index), "otherIndividual.5mld.countryOfResidenceInTheUkYesNo", mld5irts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId).url),
      bound.countryQuestion(CountryOfResidencePage(index), CountryOfResidenceInTheUkYesNoPage(index), "otherIndividual.5mld.countryOfResidence",  mld5irts.CountryOfResidenceController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(AddressYesNoPage(index), "otherIndividual.addressYesNo", irts.AddressYesNoController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(AddressUkYesNoPage(index), "otherIndividual.addressUkYesNo", irts.AddressUkYesNoController.onPageLoad(index, draftId).url),
      bound.addressQuestion(UkAddressPage(index), "site.address.uk", irts.UkAddressController.onPageLoad(index, draftId).url),
      bound.addressQuestion(NonUkAddressPage(index), "site.address.international", irts.NonUkAddressController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(PassportDetailsYesNoPage(index), "otherIndividual.passportDetailsYesNo", irts.PassportDetailsYesNoController.onPageLoad(index, draftId).url),
      bound.passportDetailsQuestion(PassportDetailsPage(index), "otherIndividual.passportDetails", irts.PassportDetailsController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(IDCardDetailsYesNoPage(index), "otherIndividual.idCardDetailsYesNo", irts.IDCardDetailsYesNoController.onPageLoad(index, draftId).url),
      bound.passportDetailsQuestion(IDCardDetailsPage(index), "otherIndividual.idCardDetails", irts.IDCardDetailsController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(MentalCapacityYesNoPage(index), "otherIndividual.5mld.mentalCapacityYesNo", mld5irts.MentalCapacityYesNoController.onPageLoad(index, draftId).url)
    ).flatten
  }
}
