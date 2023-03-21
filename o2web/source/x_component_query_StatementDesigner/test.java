        if (keys.contains(Runtime.PARAMETER_PERSON)) {
            runtime.parameters.put(Runtime.PARAMETER_PERSON, effectivePerson.getDistinguishedName());
        }
        if (keys.contains(Runtime.PARAMETER_IDENTITYLIST)) {
            runtime.parameters.put(Runtime.PARAMETER_IDENTITYLIST,
                    business.organization().identity().listWithPerson(effectivePerson));
        }
        if (keys.contains(Runtime.PARAMETER_UNITLIST)) {
            runtime.parameters.put(Runtime.PARAMETER_UNITLIST,
                    business.organization().unit().listWithPerson(effectivePerson));
        }
        if (keys.contains(Runtime.PARAMETER_UNITALLLIST)) {
            runtime.parameters.put(Runtime.PARAMETER_UNITALLLIST,
                    business.organization().unit().listWithPersonSupNested(effectivePerson));
        }
        if (keys.contains(Runtime.PARAMETER_GROUPLIST)) {
            runtime.parameters.put(Runtime.PARAMETER_GROUPLIST,
                    business.organization().group().listWithPerson(effectivePerson));
        }
        if (keys.contains(Runtime.PARAMETER_ROLELIST)) {
            runtime.parameters.put(Runtime.PARAMETER_ROLELIST,
                    business.organization().role().listWithPerson(effectivePerson));
        }